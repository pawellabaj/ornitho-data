package pl.com.labaj.ornitho.io;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static java.lang.String.format;

@Slf4j
public class PageLoader {

    public Document load(String pageUrl) {
        var connection = Jsoup.connect(pageUrl)
                .cookie("login_email", System.getProperty("login"))
                .cookie("login_password", System.getProperty("pwd"));

        LOGGER.info("Get {}", pageUrl);
        return getDocumentFromConnection(connection);
    }

    Document getDocumentFromConnection(Connection connection) {
        try {
            return connection.get();
        } catch (IOException e) {
            var url = connection.request().url();
            throw new OrnithoIOException(format("Cannot get page content from %s", url), e);
        }
    }
}
