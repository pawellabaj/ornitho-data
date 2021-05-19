package pl.com.labaj.ornitho.kraska;

import com.google.gson.Gson;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import org.w3c.dom.Document;
import pl.com.labaj.ornitho.io.DocumentLoader;
import pl.com.labaj.ornitho.io.gpx.GPXWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static io.jenetics.jpx.GPX.Version.V11;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static java.util.Comparator.comparingInt;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Kraska {

    public static final Document WAY_POINT_EXTENSIONS = new DocumentLoader().loadDocument("gpx/way-point-extensions.xml");
    public static final Document SEGMENT_EXTENSIONS = new DocumentLoader().loadDocument("gpx/segment-extensions.xml");
    public static final Document SPECIAL_SEGMENT_EXTENSIONS = new DocumentLoader().loadDocument("gpx/special-segment-extensions.xml");


    private final Gson gson= new Gson();

     public static void main(String[] args) throws FileNotFoundException, URISyntaxException {

        var kraska = new Kraska();
        kraska.run("/k/ptasi_rajd.json", "Tymoteusz Mazurkiewicz", 5614819, "ptasiRajdTymoteusz.gpx");
        kraska.run("/k/romanKempa.json", "Roman Kempa", 5663953, "romanKempa.gpx");
        kraska.run("/k/romanKempa.json", "Marcin Wojtkowiak", 5641714, "marcinWojtkowiak.gpx");
        kraska.run("/k/szymonSendera.json", "Szymon Sendera", 5651718, "szymonSendera.gpx");
        kraska.run("/k/szymonSendera.json", "Arek Furdyna", 5648290, "arekFurdyna.gpx");
        kraska.run("/k/michalRycak.json", "Michał Rycak", 5667826, "michalRycak.gpx");
        kraska.run("/k/zwirowiec.json", null, -1, "zwirowiec2.gpx");
    }

    private void run(String inputFile, String observerName, int hiddenId, String outputFile) throws FileNotFoundException, URISyntaxException {
        var file = new File(getClass().getResource(inputFile).toURI());
        var map = gson.fromJson(new FileReader(file), Map.class);

        var data = (Map<String, Object>) map.get("data");

        var sightings = ((List<Map<String, Object>>) data.get("sightings")).stream();

        var forms = data.get("forms");
        Stream<Map<String, Object>> sightingsFromForms = null;
        if (nonNull(forms)) {
            sightingsFromForms = ((List<Map<String, Object>>) forms).stream()
                    .map(form -> (List<Map<String, Object>>) form.get("sightings"))
                    .flatMap(Collection::stream);
        } else {
            sightingsFromForms = Stream.empty();
        }

        var sightingsStream = Stream.concat(sightings, sightingsFromForms);

        var gpxData = sightingsStream
                .filter(sighting -> {
                    if (isNull(observerName)) {
                        return true;
                    }

                    var observers = (List<Map<String, String>>) sighting.get("observers");
                    if (observers.size() > 1) {
                        throw new IllegalStateException("More than one observer");
                    }

                    return observers.stream()
                            .map(observer -> observer.get("name"))
                            .anyMatch(observerName::equals);
                })
                .map(sighting -> {
                    var date = LocalDateTime.parse(((Map<String, String>) sighting.get("date")).get("@ISO8601"), ISO_OFFSET_DATE_TIME);
                    var observer = ((List<Map<String, String>>) sighting.get("observers")).get(0);
                    var id = parseInt(observer.get("id_sighting"));
                    var lat = parseDouble(observer.get("coord_lat"));
                    var lon = parseDouble(observer.get("coord_lon"));

                    return new Observation(id, lat, lon, date);
                })
                .sorted(comparingInt(Observation::getId))
                .collect(GPXData.toGPXData(hiddenId));

        if (!gpxData.gapFound()) {
            System.err.println("Cannot find a gap");
            return;
        }

        var gpx = GPX.builder()
                .version(V11)
                .wayPoints(gpxData.getWayPoints())
                .addTrack(gpxData.getTrack())
                .build();

        var path = Paths.get("").resolve("output" + System.getProperty("file.separator") + outputFile);
        new GPXWriter().save(gpx, path);
    }

    private static class Observation {
        private final int id;
        private final double latitude;
        private final double longitude;
        private final LocalDateTime date;

        public Observation(int id, double latitude, double longitude, LocalDateTime date) {
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
            this.date = date;
        }

        public int getId() {
            return id;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public LocalDateTime getDate() {
            return date;
        }
    }

    private static class GPXData {
        private final int rareId;
        private final List<WayPoint> wayPoints = new ArrayList<>();
        private final TrackSegment.Builder segmentBuilder = TrackSegment.builder();

        private int previousId;
        private WayPoint previousWayPoint;
        private boolean gapFound;

        public GPXData(int rareId) {
            this.rareId = rareId;
        }

        private static Collector<Observation, GPXData, GPXData> toGPXData(int rareId) {
            return Collector.of(
                    () -> new GPXData(rareId),
                    GPXData::newObservation,
                    (gpxData1, gpxData2) -> {throw new IllegalStateException("Merging of GPXData not supported");}
            );
        }

        public void newObservation(Observation observation) {
            var currentWayPoint = WayPoint.builder()
                    .name(valueOf(observation.id))
                    .time(observation.getDate().atZone(ZoneId.systemDefault()).toInstant())
                    .sym("z-ico02")
                    .extensions(WAY_POINT_EXTENSIONS)
                    .build(observation.getLatitude(), observation.getLongitude());

            if (isNull(previousWayPoint)) {
                previousWayPoint = currentWayPoint;
                previousId = observation.getId();
                wayPoints.add(currentWayPoint);

                segmentBuilder.addPoint(WayPoint.builder().build(observation.getLatitude(), observation.getLongitude()));

                return;
            }

            if (rareId > 0 && previousId < rareId && observation.getId() > rareId) {
                var previuosWayPointWithMark = previousWayPoint.toBuilder()
                        .sym("z-ico11")
                        .build();

                wayPoints.remove(previousWayPoint);
                wayPoints.add(previuosWayPointWithMark);
                previousWayPoint = previuosWayPointWithMark;

                currentWayPoint = currentWayPoint.toBuilder()
                        .sym("z-ico11")
                        .build();

                gapFound = true;
            }

            if (previousWayPoint.getLatitude().equals(currentWayPoint.getLatitude()) && previousWayPoint.getLongitude().equals(currentWayPoint.getLongitude())) {
                return;
            }

            segmentBuilder.addPoint(WayPoint.builder().build(observation.getLatitude(), observation.getLongitude()));
            previousWayPoint = currentWayPoint;
            previousId = observation.getId();
            wayPoints.add(currentWayPoint);
        }

        public List<WayPoint> getWayPoints() {
            return wayPoints;
        }

        public Track getTrack() {
            return Track.builder()
                    .extensions(rareId > 0 ? SEGMENT_EXTENSIONS : SPECIAL_SEGMENT_EXTENSIONS)
                    .addSegment(segmentBuilder.build())
                    .build();
        }

        public boolean gapFound() {
            return gapFound;
        }
    }
}
