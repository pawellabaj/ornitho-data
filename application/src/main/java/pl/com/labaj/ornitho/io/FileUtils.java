package pl.com.labaj.ornitho.io;

import io.jenetics.jpx.XMLProvider;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pl.com.labaj.ornitho.io.gpx.GPXWriter;
import pl.com.labaj.ornitho.model.Observations;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

import static java.lang.String.format;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.notExists;

public class FileUtils {
    public static final String OUTPUT_DIRECTORY = "output";
    private final SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("ddMMyy");

    public Document loadDocument(String resourceFileName) {
        try {
            var documentBuilderFactory = XMLProvider.provider().documentBuilderFactory();
            var classLoader = GPXWriter.class.getClassLoader();
            var inputSource = new InputSource(classLoader.getResourceAsStream(resourceFileName));

            return documentBuilderFactory.newDocumentBuilder().parse(inputSource);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new OrnithoIOException("Cannot load extensions", e);
        }
    }

    public Path getPath(Observations observations, String extension) {
        createOutputDirectoryIfNeeded();

        String fileName = buildFileName(observations, extension);
        return Path.of(OUTPUT_DIRECTORY, fileName);
    }

    private void createOutputDirectoryIfNeeded() {
        try {
            var directory = Path.of(OUTPUT_DIRECTORY);
            if (notExists(directory)) {
                createDirectory(directory);
            }
        } catch (java.io.IOException e) {
            throw new OrnithoIOException(format("Cannot create %s directory", OUTPUT_DIRECTORY), e);
        }
    }

    private String buildFileName(Observations observations, String extension) {
        var dateRange = observations.getDateRange();
        var subject = observations.getSubject();

        var from = fileNameDateFormat.format(dateRange.getFrom());
        var to = fileNameDateFormat.format(dateRange.getTo());

        return new StringBuilder()
                .append(subject.replace(' ', '_'))
                .append("-")
                .append(from)
                .append("-")
                .append(to)
                .append(".")
                .append(extension)
                .toString();
    }
}
