package pl.com.labaj.ornitho.xml;

import javax.xml.parsers.DocumentBuilderFactory;

import static javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD;
import static javax.xml.XMLConstants.ACCESS_EXTERNAL_SCHEMA;

public class OrnithoXMLProvider extends io.jenetics.jpx.XMLProvider {

    @Override
    public DocumentBuilderFactory documentBuilderFactory() {
        var documentBuilderFactory = super.documentBuilderFactory();
        documentBuilderFactory.setAttribute(ACCESS_EXTERNAL_DTD, "");
        documentBuilderFactory.setAttribute(ACCESS_EXTERNAL_SCHEMA, "");
        return documentBuilderFactory;
    }
}
