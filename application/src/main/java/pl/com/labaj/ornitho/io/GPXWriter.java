package pl.com.labaj.ornitho.io;

import io.jenetics.jpx.GPX;
import lombok.extern.slf4j.Slf4j;
import pl.com.labaj.ornitho.model.DateRange;
import pl.com.labaj.ornitho.model.Observations;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.notExists;

@Slf4j
public class GPXWriter {
    private static final SimpleDateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("ddMMyy");

    public void save(Observations observations, GPX gpx) {
        var fileName = buildFileName(observations.getSpecie(), observations.getDateRange());

        saveToFile(gpx, fileName);
    }

    private void saveToFile(GPX gpx, String fileName) {
        try {
            var directory = Path.of("output");
            if (notExists(directory)) {
                createDirectory(directory);
            }

            var path = Path.of("output", fileName);
            LOGGER.debug("Write GPX to {}", path);

            GPX.write(gpx, path);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write GPX to " + fileName, e);
        }
    }

    private String buildFileName(String specie, DateRange dateRange) {
        var from = FILE_NAME_DATE_FORMAT.format(dateRange.getFrom());
        var to = FILE_NAME_DATE_FORMAT.format(dateRange.getTo());

        return new StringBuilder()
                .append(specie.replace(' ', '_'))
                .append("-")
                .append(from)
                .append("-")
                .append(to)
                .append(".gpx")
                .toString();
    }
}
