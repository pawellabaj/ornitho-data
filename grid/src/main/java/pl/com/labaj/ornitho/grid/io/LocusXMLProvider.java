package pl.com.labaj.ornitho.grid.io;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD;
import static javax.xml.XMLConstants.ACCESS_EXTERNAL_SCHEMA;

public class LocusXMLProvider extends io.jenetics.jpx.XMLProvider {

    public static Document loadExtensions() {
        try {
            var documentBuilderFactory = provider().documentBuilderFactory();
            var classLoader = GPXWriter.class.getClassLoader();
            var inputSource = new InputSource(classLoader.getResourceAsStream("locus-extensions.xml"));

            return documentBuilderFactory.newDocumentBuilder().parse(inputSource);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("Cannot create extensions", e);
        }
    }

    @Override
    public DocumentBuilderFactory documentBuilderFactory() {
        var documentBuilderFactory = super.documentBuilderFactory();
        documentBuilderFactory.setAttribute(ACCESS_EXTERNAL_DTD, "");
        documentBuilderFactory.setAttribute(ACCESS_EXTERNAL_SCHEMA, "");
        return documentBuilderFactory;
    }
}
