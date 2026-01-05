package org.openhds.hdsscapture.odk.xml;

import android.util.Log;

import org.openhds.hdsscapture.entity.Individual;
import org.openhds.hdsscapture.odk.FormUtilities;
import org.openhds.hdsscapture.odk.model.FilledForm;
import org.openhds.hdsscapture.odk.storage.access.OdkScopedDirUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Preloader for ODK form columns - generates XML with preloaded data
 * Based on HDS Explorer implementation
 */
public class OdkColumnsPreloader {

    private static final String TAG = "OdkColumnsPreloader";

    private FormUtilities formUtilities;
    private FilledForm filledForm;
    private Set<String> preloadedStartVariables;

    // Prefixes for special variables
    private final String memberPrefix = "Member.";
    private final String repeatGroupAttribute = "jr:template";

    public OdkColumnsPreloader(FormUtilities formUtilities, FilledForm filledForm) {
        this.formUtilities = formUtilities;
        this.filledForm = filledForm;
        this.preloadedStartVariables = new HashSet<>();
    }

    /**
     * Generate preloaded XML using SAF (Android 11+)
     */
    public String generatePreloadedXml(String jrFormId, String formVersion,
                                       OdkScopedDirUtil.OdkFormObject formObject) {
        try {
            return executeGeneratePreloadedXml(jrFormId, formVersion, formObject.getFormInputStream());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Form file not found", e);
        }
        return null;
    }

    /**
     * Generate preloaded XML using file path (Android 10 and below)
     */
    public String generatePreloadedXml(String jrFormId, String formVersion, String formFilePath) {
        try {
            return executeGeneratePreloadedXml(jrFormId, formVersion, new FileInputStream(formFilePath));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Form file not found: " + formFilePath, e);
        }
        return null;
    }

    /**
     * Core method to generate preloaded XML
     */
//    private String executeGeneratePreloadedXml(String jrFormId, String formVersion,
//                                               InputStream xmlInputStream) {
//        StringBuilder sbuilder = new StringBuilder();
//
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document doc = builder.parse(xmlInputStream);
//
//            // Read special variables with jr:preloadParams="start"
//            readSpecialVariables(doc);
//
//            String mainTag = jrFormId;
//            formVersion = formVersion == null ? "" : " version=\"" + formVersion + "\"";
//            Node node = doc.getElementsByTagName(mainTag).item(0);
//
//            // Fallback to "data" tag if form ID tag not found
//            if (node == null) {
//                mainTag = "data";
//                node = doc.getElementsByTagName(mainTag).item(0);
//            }
//
//            if (node != null) {
//                Log.d(TAG, "Processing main node: " + node.getNodeName());
//                sbuilder.append("<" + mainTag + " id=\"" + jrFormId + "\"" + formVersion + ">\r\n");
//
//                processNodeChildren(node, sbuilder);
//
//                Log.d(TAG, "XML generation complete");
//            } else {
//                Log.e(TAG, "ODK main tag not found: " + jrFormId);
//            }
//
//        } catch (IOException e) {
//            Log.e(TAG, "IO error parsing XML", e);
//        } catch (ParserConfigurationException e) {
//            Log.e(TAG, "Parser configuration error", e);
//        } catch (SAXException e) {
//            Log.e(TAG, "SAX parsing error", e);
//        }
//
//        return sbuilder.toString();
//    }

    private String executeGeneratePreloadedXml(String jrFormId, String formVersion,
                                               InputStream xmlInputStream) {
        StringBuilder sbuilder = new StringBuilder();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlInputStream);

            // Read special variables with jr:preloadParams="start"
            readSpecialVariables(doc);

            // Read instance_name calculation from form
            String instanceNameCalculation = readInstanceNameCalculation(doc);
            Log.d(TAG, "Instance name calculation: " + instanceNameCalculation);

            String mainTag = jrFormId;
            formVersion = formVersion == null ? "" : " version=\"" + formVersion + "\"";
            Node node = doc.getElementsByTagName(mainTag).item(0);

            // Fallback to "data" tag if form ID tag not found
            if (node == null) {
                mainTag = "data";
                node = doc.getElementsByTagName(mainTag).item(0);
            }

            if (node != null) {
                Log.d(TAG, "Processing main node: " + node.getNodeName());
                sbuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
                sbuilder.append("<" + mainTag + " id=\"" + jrFormId + "\"" + formVersion + ">\r\n");

                processNodeChildren(node, sbuilder);

                Log.d(TAG, "XML generation complete");
            } else {
                Log.e(TAG, "ODK main tag not found: " + jrFormId);
            }

        } catch (IOException e) {
            Log.e(TAG, "IO error parsing XML", e);
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "Parser configuration error", e);
        } catch (SAXException e) {
            Log.e(TAG, "SAX parsing error", e);
        }

        return sbuilder.toString();
    }

    /**
     * Read instance_name calculation from form
     * This is defined in the form's <bind> section
     */
    private String readInstanceNameCalculation(Document doc) {
        NodeList binds = doc.getElementsByTagName("bind");

        for (int i = 0; i < binds.getLength(); i++) {
            Node bindNode = binds.item(i);
            NamedNodeMap attrs = bindNode.getAttributes();

            Node nodesetNode = attrs.getNamedItem("nodeset");
            if (nodesetNode != null) {
                String nodeset = nodesetNode.getNodeValue();

                // Check if this bind is for meta/instanceName
                if (nodeset.contains("meta/instanceName") ||
                        nodeset.contains("instanceName") ||
                        nodeset.endsWith("/instance_name")) {

                    Node calculateNode = attrs.getNamedItem("calculate");
                    if (calculateNode != null) {
                        return calculateNode.getNodeValue();
                    }
                }
            }
        }

        return null;
    }

    /**
     * Read variables with jr:preloadParams="start" attribute
     */
    private void readSpecialVariables(Document doc) {
        NodeList nodes = doc.getElementsByTagName("bind");

        for (int i = 0; i < nodes.getLength(); i++) {
            Node bindNode = nodes.item(i);
            NamedNodeMap nodeAttrs = bindNode.getAttributes();
            Node preloadParamsNode = nodeAttrs.getNamedItem("jr:preloadParams");

            if (preloadParamsNode != null && "start".equals(preloadParamsNode.getNodeValue())) {
                Node nodesetNode = nodeAttrs.getNamedItem("nodeset");
                if (nodesetNode != null) {
                    String nodevalue = nodesetNode.getNodeValue();
                    String variable = nodevalue.substring(nodevalue.lastIndexOf("/") + 1);
                    this.preloadedStartVariables.add(variable);
                    Log.d(TAG, "Found start variable: " + variable);
                }
            }
        }
    }

    /**
     * Process node children recursively
     */
//    private void processNodeChildren(Node node, StringBuilder sbuilder) {
//        NodeList childElements = node.getChildNodes();
//        List<String> params = filledForm.getVariables();
//
//        for (int i = 0; i < childElements.getLength(); i++) {
//            Node n = childElements.item(i);
//
//            if (n.getNodeType() == Node.ELEMENT_NODE) {
//                String name = n.getNodeName();
//
//                // Check if this is a variable we need to preload
//                if (params.contains(name)) {
//                    handlePreloadedVariable(n, name, sbuilder);
//                }
//                // Check for special timestamp variables
//                else if (preloadedStartVariables.contains(name)) {
//                    sbuilder.append("<" + name + ">" + formUtilities.getStartTimestamp() + "</" + name + ">\r\n");
//                    Log.d(TAG, "Added start timestamp for: " + name);
//                }
//                // Check for device ID
//                else if (name.equalsIgnoreCase("deviceId")) {
//                    String deviceId = formUtilities.getDeviceId();
//                    sbuilder.append(deviceId == null ? "<" + name + " />" : "<" + name + ">" + deviceId + "</" + name + ">\r\n");
//                    Log.d(TAG, "Added device ID: " + deviceId);
//                }
//                // Check for repeat count variables
//                else if (isRepeatCountVar(name)) {
//                    // ODK auto-fills repeat count, leave as default
//                    Log.d(TAG, "Skipping repeat count variable: " + name);
//                }
//                // Handle other elements
//                else {
//                    if (!n.hasChildNodes()) {
//                        sbuilder.append("<" + name + " />\r\n");
//                    } else {
//                        if (!isRepeatGroup(n)) {
//                            sbuilder.append("<" + name + ">\r\n");
//                            processNodeChildren(n, sbuilder);
//                        }
//                    }
//                }
//            }
//        }
//
//        sbuilder.append("</" + node.getNodeName() + ">\r\n");
//    }

    private void processNodeChildren(Node node, StringBuilder sbuilder) {
        NodeList childElements = node.getChildNodes();
        List<String> params = filledForm.getVariables();
        boolean hasMetaSection = false;

        for (int i = 0; i < childElements.getLength(); i++) {
            Node n = childElements.item(i);

            if (n.getNodeType() == Node.ELEMENT_NODE) {
                String name = n.getNodeName();

                // Check if this is the meta section
                if (name.equals("meta")) {
                    hasMetaSection = true;
                    processMeta(n, sbuilder);
                }
                // Check if this is a variable we need to preload
                else if (params.contains(name)) {
                    handlePreloadedVariable(n, name, sbuilder);
                }
                // Check for special timestamp variables
                else if (preloadedStartVariables.contains(name)) {
                    sbuilder.append("<" + name + ">" + formUtilities.getStartTimestamp() + "</" + name + ">\r\n");
                    Log.d(TAG, "Added start timestamp for: " + name);
                }
                // Check for device ID
                else if (name.equalsIgnoreCase("deviceId")) {
                    String deviceId = formUtilities.getDeviceId();
                    sbuilder.append(deviceId == null ? "<" + name + " />" : "<" + name + ">" + deviceId + "</" + name + ">\r\n");
                    Log.d(TAG, "Added device ID: " + deviceId);
                }
                // Check for repeat count variables
                else if (isRepeatCountVar(name)) {
                    // ODK auto-fills repeat count, leave as default
                    Log.d(TAG, "Skipping repeat count variable: " + name);
                }
                // Handle other elements
                else {
                    if (!n.hasChildNodes()) {
                        sbuilder.append("<" + name + " />\r\n");
                    } else {
                        if (!isRepeatGroup(n)) {
                            sbuilder.append("<" + name + ">\r\n");
                            processNodeChildren(n, sbuilder);
                        }
                    }
                }
            }
        }

        sbuilder.append("</" + node.getNodeName() + ">\r\n");
    }

    /**
     * Process the meta section - this is where instanceName lives
     */
    private void processMeta(Node metaNode, StringBuilder sbuilder) {
        Log.d(TAG, "Processing meta section");
        sbuilder.append("<meta>\r\n");

        NodeList metaChildren = metaNode.getChildNodes();
        boolean hasInstanceID = false;
        boolean hasInstanceName = false;

        for (int i = 0; i < metaChildren.getLength(); i++) {
            Node child = metaChildren.item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String name = child.getNodeName();

                // Handle instanceID
                if (name.equals("instanceID")) {
                    String instanceId = "uuid:" + java.util.UUID.randomUUID().toString();
                    sbuilder.append("<instanceID>").append(instanceId).append("</instanceID>\r\n");
                    Log.d(TAG, "Generated instanceID: " + instanceId);
                    hasInstanceID = true;
                }
                // Handle instanceName - leave empty, ODK will calculate it
                else if (name.equals("instanceName")) {
                    sbuilder.append("<instanceName />\r\n");
                    Log.d(TAG, "Added empty instanceName (ODK will calculate)");
                    hasInstanceName = true;
                }
                // Handle other meta fields
                else {
                    // Check if it's a special variable
                    if (preloadedStartVariables.contains(name)) {
                        sbuilder.append("<").append(name).append(">")
                                .append(formUtilities.getStartTimestamp())
                                .append("</").append(name).append(">\r\n");
                    } else {
                        sbuilder.append("<").append(name).append(" />\r\n");
                    }
                }
            }
        }

        // Ensure required meta fields exist
        if (!hasInstanceID) {
            String instanceId = "uuid:" + java.util.UUID.randomUUID().toString();
            sbuilder.append("<instanceID>").append(instanceId).append("</instanceID>\r\n");
            Log.d(TAG, "Added missing instanceID: " + instanceId);
        }

        if (!hasInstanceName) {
            sbuilder.append("<instanceName />\r\n");
            Log.d(TAG, "Added missing instanceName");
        }

        sbuilder.append("</meta>\r\n");
    }

    /**
     * Handle preloaded variables
     */
    private void handlePreloadedVariable(Node n, String name, StringBuilder sbuilder) {
        // Check if it's a repeat group
        if (filledForm.isRepeatGroup(name)) {
            NodeList nodeRepeatChilds = n.getChildNodes();
            int count = filledForm.getRepeatGroupCount(name);

            if (count > 0) {
                // Add repeat count variable
                sbuilder.append("<" + name + "_count>" + count + "</" + name + "_count>\r\n");
            }

            // Handle different types of repeat groups
            if (filledForm.isAllMembersRepeatGroup(name)) {
                createMembers(sbuilder, name, nodeRepeatChilds,
                        filledForm.getRepeatGroupMapping(name),
                        filledForm.getHouseholdMembers());
            } else if (filledForm.isResidentMemberRepeatGroup(name)) {
                createMembers(sbuilder, name, nodeRepeatChilds,
                        filledForm.getRepeatGroupMapping(name),
                        filledForm.getResidentMembers());
            } else if (filledForm.isDeadMembersRepeatGroup(name)) {
                createMembers(sbuilder, name, nodeRepeatChilds,
                        filledForm.getRepeatGroupMapping(name),
                        filledForm.getDeadMembers());
            } else if (filledForm.isOutMigMembersRepeatGroup(name)) {
                createMembers(sbuilder, name, nodeRepeatChilds,
                        filledForm.getRepeatGroupMapping(name),
                        filledForm.getOutmigMembers());
            } else if (filledForm.getRepeatGroupType(name) ==
                    org.openhds.hdsscapture.odk.model.RepeatGroupType.MAPPED_VALUES) {
                List<Map<String, String>> mapList = filledForm.getRepeatGroupMapping(name);
                createRepeatElements(sbuilder, name, nodeRepeatChilds, mapList);
            }
        } else {
            // Normal variable - get value and add to XML
            Object value = filledForm.get(name);
            sbuilder.append(value == null ? "<" + name + " />\r\n" :
                    "<" + name + ">" + value + "</" + name + ">\r\n");
        }
    }

    /**
     * Create member repeat groups
     */
    private void createMembers(StringBuilder sbuilder, String repeatGroupName,
                               NodeList nodeRepeatChilds,
                               List<Map<String, String>> repeatGroupMappingList,
                               List<Individual> members) {
        if (nodeRepeatChilds == null || members == null || members.isEmpty()) {
            return;
        }

        Map<String, String> repeatGroupMapping = repeatGroupMappingList.get(0);

        for (Individual member : members) {
            sbuilder.append("<" + repeatGroupName + ">\r\n");

            for (int i = 0; i < nodeRepeatChilds.getLength(); i++) {
                Node n = nodeRepeatChilds.item(i);
                String name = n.getNodeName();

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (repeatGroupMapping.containsKey(name)) {
                        String mappingValue = repeatGroupMapping.get(name);
                        String var = mappingValue.replace(memberPrefix, "");
                        String memberValue = getIndividualValue(member, var);
                        sbuilder.append("<" + name + ">" + memberValue + "</" + name + ">\r\n");
                    } else {
                        if (!n.hasChildNodes()) {
                            sbuilder.append("<" + name + " />\r\n");
                        } else {
                            sbuilder.append("<" + name + ">\r\n");
                            createMemberProcessChilds(n, sbuilder, repeatGroupMapping, member);
                            sbuilder.append("</" + name + ">\r\n");
                        }
                    }
                }
            }

            sbuilder.append("</" + repeatGroupName + ">\r\n");
        }
    }

    /**
     * Process child nodes for members
     */
    private void createMemberProcessChilds(Node node, StringBuilder sbuilder,
                                           Map<String, String> repeatGroupMapping,
                                           Individual member) {
        NodeList childElements = node.getChildNodes();

        for (int i = 0; i < childElements.getLength(); i++) {
            Node n = childElements.item(i);
            String name = n.getNodeName();

            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if (repeatGroupMapping.containsKey(name)) {
                    String mappingValue = repeatGroupMapping.get(name);
                    String var = mappingValue.replace(memberPrefix, "");
                    String memberValue = getIndividualValue(member, var);
                    sbuilder.append("<" + name + ">" + memberValue + "</" + name + ">\r\n");
                } else {
                    if (!n.hasChildNodes()) {
                        sbuilder.append("<" + name + " />\r\n");
                    } else {
                        sbuilder.append("<" + name + ">\r\n");
                        createMemberProcessChilds(n, sbuilder, repeatGroupMapping, member);
                        sbuilder.append("</" + name + ">\r\n");
                    }
                }
            }
        }
    }

    /**
     * Create repeat elements from mapped values
     */
    private void createRepeatElements(StringBuilder sbuilder, String repeatGroupName,
                                      NodeList nodeRepeatChilds,
                                      List<Map<String, String>> repeatGroupMappingList) {
        if (nodeRepeatChilds == null) return;

        for (Map<String, String> repeatGroupMapping : repeatGroupMappingList) {
            sbuilder.append("<" + repeatGroupName + ">\r\n");

            for (int i = 0; i < nodeRepeatChilds.getLength(); i++) {
                Node n = nodeRepeatChilds.item(i);
                String name = n.getNodeName();

                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    if (repeatGroupMapping.containsKey(name)) {
                        String value = repeatGroupMapping.get(name);
                        sbuilder.append("<" + name + ">" + value + "</" + name + ">\r\n");
                    } else {
                        if (!n.hasChildNodes()) {
                            sbuilder.append("<" + name + " />\r\n");
                        } else {
                            sbuilder.append("<" + name + ">\r\n");
                            createRepeatElementsProcessChilds(n, sbuilder, repeatGroupMapping);
                            sbuilder.append("</" + name + ">\r\n");
                        }
                    }
                }
            }

            sbuilder.append("</" + repeatGroupName + ">\r\n");
        }
    }

    /**
     * Process child nodes for repeat elements
     */
    private void createRepeatElementsProcessChilds(Node node, StringBuilder sbuilder,
                                                   Map<String, String> repeatGroupMapping) {
        NodeList childElements = node.getChildNodes();

        for (int i = 0; i < childElements.getLength(); i++) {
            Node n = childElements.item(i);
            String name = n.getNodeName();

            if (n.getNodeType() == Node.ELEMENT_NODE) {
                if (repeatGroupMapping.containsKey(name)) {
                    String value = repeatGroupMapping.get(name);
                    sbuilder.append("<" + name + ">" + value + "</" + name + ">\r\n");
                } else {
                    if (!n.hasChildNodes()) {
                        sbuilder.append("<" + name + " />\r\n");
                    } else {
                        sbuilder.append("<" + name + ">\r\n");
                        createRepeatElementsProcessChilds(n, sbuilder, repeatGroupMapping);
                        sbuilder.append("</" + name + ">\r\n");
                    }
                }
            }
        }
    }

    /**
     * Get value from Individual object by field name
     */
    private String getIndividualValue(Individual member, String fieldName) {
        try {
            switch (fieldName.toLowerCase()) {
                case "uuid":
                    return member.uuid != null ? member.uuid : "";
                case "extid":
                    return member.extId != null ? member.extId : "";
                case "firstname":
                    return member.firstName != null ? member.firstName : "";
                case "othername":
                    return member.otherName != null ? member.otherName : "";
                case "lastname":
                    return member.lastName != null ? member.lastName : "";
                case "gender":
                    return member.gender != null ? String.valueOf(member.gender) : "";
                case "dob":
                    return member.getDob() != null ? member.getDob() : "";
                case "age":
                    return String.valueOf(member.getAge());
                default:
                    return "";
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting value for field: " + fieldName, e);
            return "";
        }
    }

    /**
     * Check if node is a repeat group
     */
    private boolean isRepeatGroup(Node node) {
        NamedNodeMap map = node.getAttributes();
        Node n = (map != null) ? map.getNamedItem(repeatGroupAttribute) : null;
        return n != null;
    }

    /**
     * Check if variable is a repeat count variable
     */
    private boolean isRepeatCountVar(String nodeName) {
        if (nodeName != null && nodeName.endsWith("_count")) {
            return filledForm.isMemberRepeatGroup(nodeName.replace("_count", ""));
        }
        return false;
    }
}