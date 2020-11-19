package pl.com.labaj.ornitho.io.gpx;

import io.jenetics.jpx.GPX;
import lombok.extern.slf4j.Slf4j;
import pl.com.labaj.ornitho.io.OrnithoIOException;

import java.io.IOException;
import java.nio.file.Path;

import static java.lang.String.format;

@Slf4j
public class GPXWriter {
    public void save(GPX gpx, Path path) {
        try {
            LOGGER.debug("Write GPX to {}", path);
            GPX.write(gpx, path);
        } catch (IOException e) {
            throw new OrnithoIOException(format("Cannot write GPX to %s file", path.getFileName()), e);
        }
    }
}
