package pl.com.labaj.ornitho.io;

import io.jenetics.jpx.XMLProvider;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class DocumentLoader {
    public Document loadDocument(String resourceFileName) {
        try {
            var documentBuilderFactory = XMLProvider.provider().documentBuilderFactory();
            var classLoader = DocumentLoader.class.getClassLoader();
            var inputSource = new InputSource(classLoader.getResourceAsStream(resourceFileName));

            return documentBuilderFactory.newDocumentBuilder().parse(inputSource);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new OrnithoIOException("Cannot load document", e);
        }
    }
}
