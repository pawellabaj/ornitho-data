package pl.com.labaj.ornitho.grid.core;

import com.grum.geocalc.Point;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pl.com.labaj.ornitho.grid.model.Location;

import java.util.stream.Stream;

import static com.grum.geocalc.EarthCalc.pointAt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@Slf4j
public class LocationAnalyzer implements Analyzer<Location> {
    private static final String SELECTOR_LOCATION_CELL = "#td-main-table > tbody > tr > td > div.box > table > tbody > tr:nth-child(1) > td:nth-child(2)";
    private static final double DISTANCE = Math.sqrt(2) * 500;

    private final PositionParser positionParser;

    public LocationAnalyzer(PositionParser positionParser) {this.positionParser = positionParser;}

    @Override
    public Location apply(Document locationPage) {
        var locationCell = locationPage.select(SELECTOR_LOCATION_CELL);
        return buildLocation(locationCell.first());
    }

    private Location buildLocation(Element row) {
        var name = row.child(0).text();

        var centerPosition = parsePosition(row.childNode(3).outerHtml());
        var center = toWayPoint(centerPosition);
        var segment = toTrackSegment(centerPosition);
        LOGGER.debug("Center: {}, Segment: {}", center, segment);

        return new Location(name, center, segment);
    }

    private TrackSegment toTrackSegment(Point centerPosition) {
        var point1 = pointAt(centerPosition, 45, DISTANCE);
        var point2 = pointAt(centerPosition, 135, DISTANCE);
        var point3 = pointAt(centerPosition, 225, DISTANCE);
        var point4 = pointAt(centerPosition, 315, DISTANCE);

        return Stream.of(point1, point2, point3, point4, point1)
                .map(this::toWayPoint)
                .collect(collectingAndThen(toList(), TrackSegment::of));
    }

    private Point parsePosition(String position) {
        var coordinates = position.split("/");
        return positionParser.parse(coordinates);
    }

    private WayPoint toWayPoint(Point point) {
        return WayPoint.of(point.latitude, point.longitude);
    }
}
