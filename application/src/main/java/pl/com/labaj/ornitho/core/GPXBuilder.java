package pl.com.labaj.ornitho.core;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.WayPoint;
import org.w3c.dom.Document;
import pl.com.labaj.ornitho.io.LocusXMLProvider;
import pl.com.labaj.ornitho.model.Location;
import pl.com.labaj.ornitho.model.Observations;

import java.text.SimpleDateFormat;
import java.util.List;

import static io.jenetics.jpx.GPX.Version.V11;
import static java.util.stream.Collectors.toList;

public class GPXBuilder {

    private static final SimpleDateFormat DESCRIPTION_DATE_FORMAT = new SimpleDateFormat("d.MM.yyyy");
    private static final Document EXTENSIONS = LocusXMLProvider.loadExtensions();

    public GPX build(Observations observations) {
        return GPX.builder()
                .version(V11)
                .metadata(buildMetadata(observations))
                .wayPoints(buildWayPoints(observations.getLocations()))
                .tracks(buildTracks(observations.getLocations()))
                .build();
    }

    private Metadata buildMetadata(Observations observations) {
        return Metadata.builder()
                .desc(buildDescription(observations))
                .build();
    }

    private List<WayPoint> buildWayPoints(List<Location> locations) {
        return locations.stream()
                .map(this::toWayPoint)
                .collect(toList());
    }

    private List<Track> buildTracks(List<Location> locations) {
        return locations.stream()
                .map(this::toTrack)
                .collect(toList());
    }

    private String buildDescription(Observations observations) {
        var from = DESCRIPTION_DATE_FORMAT.format(observations.getDateRange().getFrom());
        var to = DESCRIPTION_DATE_FORMAT.format(observations.getDateRange().getTo());

        return new StringBuilder()
                .append(observations.getSpecie())
                .append(", od ")
                .append(from)
                .append(" do ")
                .append(to)
                .toString();
    }

    private WayPoint toWayPoint(Location location) {
        return WayPoint.of(location.getCenter())
                .toBuilder()
                .name(location.getName())
                .sym("z-ico02")
                .build();
    }

    private Track toTrack(Location location) {
        return Track.builder()
                .name(location.getName())
                .addSegment(location.getSegment())
                .extensions(EXTENSIONS)
                .build();
    }
}
