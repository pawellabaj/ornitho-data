package pl.com.labaj.ornitho.util;

import org.jsoup.helper.DataUtil;
import org.jsoup.nodes.Document;
import pl.com.labaj.ornitho.io.OrnithoIOException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class HtmlReader {
    public static Document readHtmlFromFile(String fileName) {
        try {
            var classLoader = HtmlReader.class.getClassLoader();
            var resourceUrl = classLoader.getResource(fileName);
            var file = new File(resourceUrl.toURI());
            return DataUtil.load(file, "utf-8", ".");
        } catch (URISyntaxException | IOException e) {
            throw new OrnithoIOException(String.format("Cannot read HTML from %s", fileName), e);
        }
    }
}
