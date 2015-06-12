package com.rsr5.gocd.riemann_notifier;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import com.thoughtworks.go.plugin.api.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;

public class PipelineStatePopulator {

    private static Logger LOGGER = Logger.getLoggerFor(PipelineStatePopulator
            .class);

    protected RetrievePipelineRSS retrievePipelineRSS = new RetrievePipelineRSS();

    public HashMap<String, String> getStageStates() throws IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOGGER.warn("XML Parser Incorrectly configured.");
            return null;
        }
        HttpURLConnection request = retrievePipelineRSS.download();

        Document doc = null;
        try {
            doc = dBuilder.parse((InputStream) request.getContent());
        } catch (SAXException e) {
            LOGGER.warn("Cannot parse RSS XML.");
            return null;
        }

        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName("Project");

        HashMap<String, String> pipelineStates = new HashMap<>();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String name = eElement.getAttribute("name");
                String[] parts = name.split(" :: ");
                String pipeline;
                if (parts.length == 2) {
                    pipeline = parts[0] + ":" + parts[1];
                    pipelineStates.put(pipeline,
                            eElement.getAttribute("activity"));
                }
            }
        }

        return pipelineStates;
    }
}
