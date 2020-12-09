package pl.com.labaj.ornitho.io;

import pl.com.labaj.ornitho.model.Observations;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

import static java.lang.String.format;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.notExists;

public class PathGenerator {
    private static final String OUTPUT_DIRECTORY = "output";
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("ddMMyy");

    private Path contextPath = Paths.get("");

    public Path getPath(Observations observations, String extension) {
        createOutputDirectoryIfNeeded();

        String fileName = buildFileName(observations, extension);
        return contextPath.resolve(OUTPUT_DIRECTORY + FILE_SEPARATOR + fileName);
    }

    void setContextPath(Path contextPath) {
        this.contextPath = contextPath;
    }

    private void createOutputDirectoryIfNeeded() {
        try {
            var directory = contextPath.resolve(OUTPUT_DIRECTORY);
            if (notExists(directory)) {
                createDirectory(directory);
            }
        } catch (IOException e) {
            throw new OrnithoIOException(format("Cannot create %s directory", OUTPUT_DIRECTORY), e);
        }
    }

    private String buildFileName(Observations observations, String extension) {
        var dateRange = observations.getDateRange();
        var subject = observations.getSubject();

        return new StringBuilder()
                .append(subject.replace(' ', '_'))
                .append("-")
                .append(DATE_TIME_FORMATTER.format(dateRange.getFrom()))
                .append("-")
                .append(DATE_TIME_FORMATTER.format(dateRange.getTo()))
                .append(".")
                .append(extension)
                .toString();
    }
}
