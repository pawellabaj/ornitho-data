package pl.com.labaj.ornitho.io.page;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pl.com.labaj.ornitho.io.OrnithoIOException;

import java.io.IOException;

import static java.lang.String.format;

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
            throw new OrnithoIOException(format("Cannot get page content from %s", pageUrl), e);
        }
    }
}
