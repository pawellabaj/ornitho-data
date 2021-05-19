package pl.com.labaj.ornitho.kraska;

import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import org.w3c.dom.Document;
import pl.com.labaj.ornitho.io.DocumentLoader;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

import static java.lang.Integer.MIN_VALUE;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;

class GPXData {
    private static final Document WAY_POINT_EXTENSIONS = new DocumentLoader().loadDocument("gpx/way-point-extensions.xml");
    private static final Document LINE_EXTENSIONS = new DocumentLoader().loadDocument("gpx/direct-line-extensions.xml");
    private static final Document HIDDEN_LINE_EXTENSIONS = new DocumentLoader().loadDocument("gpx/hidden-line-extensions.xml");

    private final int hiddenId;
    private final List<WayPoint> wayPoints = new ArrayList<>();
    private final TrackSegment.Builder segmentBuilder = TrackSegment.builder();

    private int previousId;
    private WayPoint previousWayPoint;
    private boolean gapFound;

    public static Collector<Observation, GPXData, GPXData> toGPXData() {
        return toGPXData(MIN_VALUE);
    }

    public static Collector<Observation, GPXData, GPXData> toGPXData(int hiddenId) {
        return Collector.of(
                () -> new GPXData(hiddenId),
                GPXData::newObservation,
                (gpxData1, gpxData2) -> {throw new IllegalStateException("Merging of GPXData not supported");}
        );
    }

    private GPXData(int hiddenId) {
        this.hiddenId = hiddenId;
    }

    public List<WayPoint> getWayPoints() {
        return wayPoints;
    }

    public Track getTrack() {
        return Track.builder()
                .extensions(hiddenId < 0 ? HIDDEN_LINE_EXTENSIONS : LINE_EXTENSIONS)
                .addSegment(segmentBuilder.build())
                .build();
    }

    public boolean gapFound() {
        return gapFound;
    }

    private void newObservation(Observation observation) {
        var currentWayPoint = currentPoint(observation);

        if (isNull(previousWayPoint)) {
            addSegment(observation, currentWayPoint);
            return;
        }

        if (previousId < hiddenId && observation.getId() > hiddenId) {
            markPreviousPoint();
            currentWayPoint = markPoint(currentWayPoint);
        }

        if (sameCoordinatesAsPrevious(currentWayPoint)) {
            return;
        }

        addSegment(observation, currentWayPoint);
    }

    private void markPreviousPoint() {
        gapFound = true;

        var previousWayPointWithMark = markPoint(previousWayPoint);

        wayPoints.remove(previousWayPoint);
        wayPoints.add(previousWayPointWithMark);
        previousWayPoint = previousWayPointWithMark;
    }

    private WayPoint currentPoint(Observation observation) {
        return WayPoint.builder()
                .name(valueOf(observation.getId()))
                .time(observation.getDate().atZone(ZoneId.systemDefault()).toInstant())
                .sym("z-ico02")
                .extensions(WAY_POINT_EXTENSIONS)
                .build(observation.getLatitude(), observation.getLongitude());
    }

    private WayPoint markPoint(WayPoint wayPoint) {
        return wayPoint.toBuilder()
                .sym("z-ico11")
                .build();
    }

    private void addSegment(Observation observation, WayPoint currentWayPoint) {
        previousWayPoint = currentWayPoint;
        previousId = observation.getId();
        wayPoints.add(currentWayPoint);
        segmentBuilder.addPoint(trackPoint(observation));
    }

    private WayPoint trackPoint(Observation observation) {
        return WayPoint.builder().build(observation.getLatitude(), observation.getLongitude());
    }

    private boolean sameCoordinatesAsPrevious(WayPoint currentWayPoint) {
        return previousWayPoint.getLatitude().equals(currentWayPoint.getLatitude()) && previousWayPoint.getLongitude().equals(currentWayPoint.getLongitude());
    }
}
