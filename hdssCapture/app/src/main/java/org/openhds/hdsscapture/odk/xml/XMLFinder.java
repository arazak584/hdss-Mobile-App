package org.openhds.hdsscapture.odk.xml;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Utility to find and parse ODK form definitions from XML files
 */
public class XMLFinder {

    private static final String TAG = "XMLFinder";

    /**
     * Extract ODK form metadata from XML file
     */
    public static XFormDef getODKForm(File file) {

        if (!file.exists()) {
            Log.e(TAG, "File does not exist: " + file.getAbsolutePath());
            return null;
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            // Process XML securely, avoid XXE attacks
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);

            // Normalize document
            doc.getDocumentElement().normalize();

            String formId = null;
            String formName = null;
            String formVersion = null;

            // Get title nodes
            NodeList titleNodes = doc.getElementsByTagName("h:title");
            NodeList instanceNodes = doc.getElementsByTagName("instance");

            // Extract form name from title
            if (titleNodes.getLength() > 0) {
                Node titleNode = titleNodes.item(0);
                formName = titleNode.getTextContent();
            }

            // Extract form ID and version from instance
            if (instanceNodes.getLength() > 0) {
                Node nodeInstance = instanceNodes.item(0);
                NodeList instanceChilds = nodeInstance.getChildNodes();

                // Find the main form node (first element child)
                for (int i = 0; i < instanceChilds.getLength(); i++) {
                    Node mainFormNode = instanceChilds.item(i);

                    if (mainFormNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element elementMainNode = (Element) mainFormNode;
                        formId = elementMainNode.getAttribute("id");
                        formVersion = elementMainNode.getAttribute("version");

                        Log.d(TAG, "Found form: id=" + formId + ", version=" + formVersion);
                        break;
                    }
                }
            }

            if (formId != null) {
                return new XFormDef(formId, formName, formVersion, file.getAbsolutePath());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing ODK form: " + file.getName(), e);
        }

        return null;
    }
}