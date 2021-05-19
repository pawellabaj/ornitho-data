package pl.com.labaj.ornitho.kraska;

import com.google.gson.Gson;
import io.jenetics.jpx.GPX;
import pl.com.labaj.ornitho.io.gpx.GPXWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.jenetics.jpx.GPX.Version.V11;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.util.Comparator.comparingInt;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ObservationsFinder {

    private final Gson gson = new Gson();

    public static void main(String[] args) throws FileNotFoundException, URISyntaxException {

        var finder = new ObservationsFinder();

        finder.findGap("/k/ptasi_rajd.json", "Tymoteusz Mazurkiewicz", 5614819, "ptasiRajdTymoteusz.gpx");
        finder.findGap("/k/romanKempa.json", "Roman Kempa", 5663953, "romanKempa.gpx");
        finder.findGap("/k/romanKempa.json", "Marcin Wojtkowiak", 5641714, "marcinWojtkowiak.gpx");
        finder.findGap("/k/szymonSendera.json", "Szymon Sendera", 5651718, "szymonSendera.gpx");
        finder.findGap("/k/szymonSendera.json", "Arek Furdyna", 5648290, "arekFurdyna.gpx");
        finder.findGap("/k/michalRycak.json", "Michał Rycak", 5667826, "michalRycak.gpx");

        finder.findPoints("/k/zwirowiec.json", "zwirowiec.gpx");
    }

    public void findGap(String inputFile, String observerName, int hiddenId, String outputFile) throws FileNotFoundException, URISyntaxException {
        var gpxData = getSightingsStream(inputFile)
                .filter(sighting -> matchedObserver(observerName, sighting))
                .map(this::toObservation)
                .sorted(comparingInt(Observation::getId))
                .collect(GPXData.toGPXData(hiddenId));

        if (!gpxData.gapFound()) {
            System.err.println("Cannot find a gap");
            return;
        }

        saveGPXToFile(gpxData, outputFile);
    }

    public void findPoints(String inputFile, String outputFile) throws FileNotFoundException, URISyntaxException {
        var gpxData = getSightingsStream(inputFile)
                .map(this::toObservation)
                .collect(GPXData.toGPXData());

        saveGPXToFile(gpxData, outputFile);
    }

    private Map<String, String> getObserver(Map<String, Object> sighting) {
        var observers = (List<Map<String, String>>) sighting.get("observers");
        if (observers.size() > 1) {
            throw new IllegalStateException("More than one observer");
        }
        return observers.get(0);
    }

    private boolean matchedObserver(String observerName, Map<String, Object> sighting) {
        if (isNull(observerName)) {
            return true;
        }
        return getObserver(sighting).get("name").equals(observerName);
    }

    private Observation toObservation(Map<String, Object> sighting) {
        var date = LocalDateTime.parse(((Map<String, String>) sighting.get("date")).get("@ISO8601"), ISO_OFFSET_DATE_TIME);
        Map<String, String> observer = getObserver(sighting);
        var id = parseInt(observer.get("id_sighting"));
        var lat = parseDouble(observer.get("coord_lat"));
        var lon = parseDouble(observer.get("coord_lon"));

        return new Observation(id, lat, lon, date);
    }

    private void saveGPXToFile(GPXData gpxData, String outputFile) {
        var gpx = GPX.builder()
                .version(V11)
                .wayPoints(gpxData.getWayPoints())
                .addTrack(gpxData.getTrack())
                .build();

        var path = Paths.get("").resolve("output" + System.getProperty("file.separator") + outputFile);
        new GPXWriter().save(gpx, path);
    }

    private Stream<Map<String, Object>> getSightingsStream(String inputFile) throws URISyntaxException, FileNotFoundException {
        var file = new File(getClass().getResource(inputFile).toURI());
        var map = gson.fromJson(new FileReader(file), Map.class);

        var data = (Map<String, Object>) map.get("data");

        var sightings = (List<Map<String, Object>>) data.get("sightings");
        Stream<Map<String, Object>> sightingsStream = nonNull(sightings) ? sightings.stream() : Stream.empty();

        var forms = (List<Map<String, Object>>) data.get("forms");
        Stream<Map<String, Object>> sightingsFromForms = nonNull(forms) ? forms.stream()
                .map(form -> (List<Map<String, Object>>) form.get("sightings"))
                .flatMap(Collection::stream) : Stream.empty();

        return Stream.concat(sightingsStream, sightingsFromForms);
    }
}
