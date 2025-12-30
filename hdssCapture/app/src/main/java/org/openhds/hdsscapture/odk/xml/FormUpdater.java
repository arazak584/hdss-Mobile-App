package org.openhds.hdsscapture.odk.xml;

import android.content.ContentValues;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Utility to update ODK form XML files with preloaded values
 */
public class FormUpdater {

    private static final String TAG = "FormUpdater";

    private File xmlFile;
    private ContentValues values;

    public FormUpdater() {
        values = new ContentValues();
    }

    public FormUpdater(File xmlFile) {
        this();
        this.xmlFile = xmlFile;
    }

    public FormUpdater(String xmlFile) {
        this(new File(xmlFile));
    }

    public FormUpdater(File xmlFile, ContentValues contentValues) {
        this(xmlFile);
        this.values.putAll(contentValues);
    }

    public FormUpdater(String xmlFile, ContentValues contentValues) {
        this(new File(xmlFile), contentValues);
    }

    public void setContentValues(ContentValues contentValues) {
        this.values.putAll(contentValues);
    }

    public void setContentValues(Map<String, Object> mapValues) {
        for (String key : mapValues.keySet()) {
            Object value = mapValues.get(key);
            if (value != null) {
                this.values.put(key, value.toString());
            }
        }
    }

    public void update() {
        if (xmlFile != null && xmlFile.exists()) {
            openAndUpdateXML(this.xmlFile, this.values);
        } else {
            Log.e(TAG, "XML file is null or doesn't exist");
        }
    }

    private void openAndUpdateXML(File formXmlFile, ContentValues cvs) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(formXmlFile);

            Element root = doc.getDocumentElement();
            updateElementNodes(root, cvs);

            // Save XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new FileWriter(formXmlFile));
            transformer.transform(source, result);

            Log.d(TAG, "XML updated successfully: " + formXmlFile.getName());

        } catch (Exception e) {
            Log.e(TAG, "Error updating XML file", e);
        }
    }

    private void updateElementNodes(Element node, ContentValues cvs) {
        NodeList children = node.getChildNodes();
        List<String> params = new ArrayList<>(cvs.keySet());

        for (int i = 0; i < children.getLength(); i++) {
            org.w3c.dom.Node child = children.item(i);

            if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element element = (Element) child;
                String name = element.getTagName();

                if (element.getChildNodes().getLength() == 1 &&
                        element.getFirstChild().getNodeType() == org.w3c.dom.Node.TEXT_NODE) {
                    // Leaf node with text content

                    if (params.contains(name)) {
                        String value = cvs.getAsString(name);
                        if (value != null) {
                            element.setTextContent(value);
                            Log.d(TAG, "Updated: " + name + " = " + value);
                        }
                    }
                } else if (element.getChildNodes().getLength() > 1 ||
                        (element.getChildNodes().getLength() == 1 &&
                                element.getFirstChild().getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)) {
                    // Has child elements, recurse
                    updateElementNodes(element, cvs);
                }
            }
        }
    }
}