package pl.com.labaj.ornitho.io.gpx;

import com.grum.geocalc.BoundingArea;
import com.grum.geocalc.Point;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import org.w3c.dom.Document;
import pl.com.labaj.ornitho.io.FileUtils;
import pl.com.labaj.ornitho.model.Location;
import pl.com.labaj.ornitho.model.Observations;

import java.nio.file.Path;
import java.util.List;

import static io.jenetics.jpx.GPX.Version.V11;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public class GPXBuilder {

    private final GPXWriter gpxWriter;

    private final Document waypointExtensions;
    private final Document lineExtensions;

    public GPXBuilder(FileUtils fileUtils, GPXWriter gpxWriter) {
        this.gpxWriter = gpxWriter;

        waypointExtensions = fileUtils.loadDocument("gpx/waypoint-extensions.xml");
        lineExtensions = fileUtils.loadDocument("gpx/line-extensions.xml");
    }

    public void buildAndSave(Observations observations, Path path) {
        var gpx = buildGPX(observations);
        gpxWriter.save(gpx, path);
    }

    private GPX buildGPX(Observations observations) {
        return GPX.builder()
                .version(V11)
                .metadata(buildMetadata(observations))
                .wayPoints(buildWayPoints(observations.getLocations()))
                .tracks(buildTracks(observations.getLocations()))
                .build();
    }

    private Metadata buildMetadata(Observations observations) {
        return Metadata.builder()
                .desc(observations.getDescription())
                .build();
    }

    private List<WayPoint> buildWayPoints(List<Location> locations) {
        return locations.stream()
                .map(this::toWayPoint)
                .collect(toList());
    }

    private List<Track> buildTracks(List<Location> locations) {
        var builder = Track.builder();

        locations.stream()
                .map(Location::getBoundingArea)
                .map(this::buildSegment)
                .forEach(builder::addSegment);

        var track = builder
                .extensions(lineExtensions)
                .build();
        return singletonList(track);
    }

    private WayPoint toWayPoint(Location location) {
        var center = location.getCenter();

        return WayPoint.builder()
                .lat(center.latitude)
                .lon(center.longitude)
                .name(location.getName())
                .sym("z-ico02")
                .extensions(waypointExtensions)
                .build();
    }

    private TrackSegment buildSegment(BoundingArea boundingArea) {
        return TrackSegment.builder()
                .addPoint(getWayPoint(boundingArea.northEast))
                .addPoint(getWayPoint(boundingArea.southEast))
                .addPoint(getWayPoint(boundingArea.southWest))
                .addPoint(getWayPoint(boundingArea.northWest))
                .addPoint(getWayPoint(boundingArea.northEast))
                .build();
    }

    private WayPoint getWayPoint(Point northEast) {
        return WayPoint.of(northEast.latitude, northEast.longitude);
    }
}
