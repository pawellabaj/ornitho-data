package pl.com.labaj.ornitho.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DocumentBuilder {

    private static final javax.xml.parsers.DocumentBuilder documentBuilder;

    static {
        try {
            documentBuilder = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Cannot create document builder", e);
        }
    }

    private DocumentBuilder() {}

    public static Document buildDocument(String rootName, String... tags) {
        var document = documentBuilder.newDocument();
        var root = document.createElement(rootName);
        document.appendChild(root);

        Element parent = root;
        for (String tag : tags) {
            var element = document.createElement(tag);
            parent.appendChild(element);
            parent = element;
        }

        return document;
    }
}
