package pl.com.labaj.ornitho.io;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

@Slf4j
public class PageLoader {

    public Document load(String pageUrl) {
        LOGGER.debug("Connecting to {}", pageUrl);
        var connection = Jsoup.connect(pageUrl)
                .cookie("login_email", System.getProperty("login"))
                .cookie("login_password", System.getProperty("pwd"));

        try {
            return connection.get();
        } catch (IOException e) {
            throw new RuntimeException("Cannot get page content from " + pageUrl, e);
        }
    }
}
