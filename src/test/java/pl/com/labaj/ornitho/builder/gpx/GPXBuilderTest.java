package pl.com.labaj.ornitho.builder.gpx;

import com.grum.geocalc.BoundingArea;
import com.grum.geocalc.Point;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Longitude;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import pl.com.labaj.ornitho.io.gpx.GPXExtensions;
import pl.com.labaj.ornitho.io.gpx.GPXWriter;
import pl.com.labaj.ornitho.model.DateRange;
import pl.com.labaj.ornitho.model.Location;
import pl.com.labaj.ornitho.model.Observations;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.stream.IntStream;

import static com.grum.geocalc.Coordinate.fromDegrees;
import static io.jenetics.jpx.GPX.Version.V11;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.com.labaj.ornitho.util.Assertions.listAssertions;
import static pl.com.labaj.ornitho.util.DocumentBuilder.buildDocument;
import static pl.com.labaj.ornitho.util.DocumentTagNamesComparator.documentComparator;

@ExtendWith(MockitoExtension.class)
class GPXBuilderTest {
    public static final String DESCRIPTION = "description";

    public static final String[] POINT_NAMES = {"Alfa", "Bravo", "Charlie"};
    private static final double[] NORTH_EAST_LATS = {0d, 10d, -40d};
    private static final double[] NORTH_EAST_LONS = {0d, 20d, -10d};
    private static final double[] SOUTH_WEST_LATS = {30d, -20d, 0d};
    private static final double[] SOUTH_WEST_LONS = {60d, 10d, 0d};
    private static final double[] CENTER_LATS = {15d, -5d, -20d};
    private static final double[] CENTER_LONS = {30d, 15d, -5d};

    private static final Document WAYPOINT_EXTENSIONS = buildDocument("extensions", "wayPoint");
    private static final Document LINE_EXTENSIONS = buildDocument("extensions", "line");

    @Mock
    GPXExtensions gpxExtensions;

    @Mock
    GPXWriter gpxWriter;

    @InjectMocks
    GPXBuilder gpxBuilder;

    @Captor
    ArgumentCaptor<GPX> gpxCaptor;

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldBuildGpx() {
        //given
        when(gpxExtensions.getWaypointExtensions()).thenReturn(WAYPOINT_EXTENSIONS);
        when(gpxExtensions.getLineExtensions()).thenReturn(LINE_EXTENSIONS);

        var observations = createObservations();
        var path = Path.of("path");

        //when
        gpxBuilder.buildAndSave(observations, path);

        //then
        verify(gpxWriter).save(gpxCaptor.capture(), any(Path.class));
        var gpx = gpxCaptor.getValue();

        assertSoftly(softly -> {
            softly.assertThat(gpx.getVersion()).as("version")
                    .isEqualTo(V11.getValue());
            softly.assertThat(gpx.getMetadata()).as("metadata")
                    .hasValueSatisfying(this::metadataAssertion);
            softly.assertThat(gpx.getWayPoints()).as("waypoints")
                    .hasSize(POINT_NAMES.length)
                    .satisfies(listAssertions(this::wayPointAssertion));
            softly.assertThat(gpx.getTracks()).as("tracks")
                    .singleElement()
                    .satisfies(this::trackAssertion);
        });
    }

    @Test
    void shouldWriteGpx() {
        //given
        var observations = createObservations();
        var path = temporaryDirectory.resolve("testGpxBuilder.gpx");

        when(gpxWriter.save(any(GPX.class), eq(path))).thenCallRealMethod();

        //when
        gpxBuilder.buildAndSave(observations, path);

        //then
        assertThat(path).exists();
    }

    private void metadataAssertion(Metadata metadata) {
        assertThat(metadata.getDescription()).hasValue(DESCRIPTION);
    }

    private void wayPointAssertion(WayPoint wayPoint, int atIndex) {
        assertThat(wayPoint.getName()).hasValue(POINT_NAMES[atIndex]);
        assertThat(wayPoint.getSymbol()).hasValue("z-ico02");
        assertThat(wayPoint.getLatitude()).isEqualTo(Latitude.ofDegrees(CENTER_LATS[atIndex]));
        assertThat(wayPoint.getLongitude()).isEqualTo(Longitude.ofDegrees(CENTER_LONS[atIndex]));
        assertThat(wayPoint.getExtensions()).usingValueComparator(documentComparator()).hasValue(WAYPOINT_EXTENSIONS);
    }

    private void trackAssertion(Track track) {
        assertThat(track.getExtensions()).usingValueComparator(documentComparator()).hasValue(LINE_EXTENSIONS);
        assertThat(track.getSegments())
                .hasSize(POINT_NAMES.length)
                .satisfies(listAssertions(this::segmentAssertion));
    }

    private void segmentAssertion(TrackSegment segment, int atIndex) {
        assertThat(segment).contains(
                WayPoint.of(NORTH_EAST_LATS[atIndex], NORTH_EAST_LONS[atIndex]),
                WayPoint.of(SOUTH_WEST_LATS[atIndex], NORTH_EAST_LONS[atIndex]),
                WayPoint.of(SOUTH_WEST_LATS[atIndex], SOUTH_WEST_LONS[atIndex]),
                WayPoint.of(NORTH_EAST_LATS[atIndex], SOUTH_WEST_LONS[atIndex]),
                WayPoint.of(NORTH_EAST_LATS[atIndex], NORTH_EAST_LONS[atIndex]));
    }

    private Observations createObservations() {
        var locations = IntStream.range(0, POINT_NAMES.length)
                .mapToObj(this::createLocation)
                .collect(toList());

        return new Observations(
                "subject",
                DESCRIPTION,
                new DateRange(LocalDate.now(), LocalDate.now()),
                locations);
    }

    private Location createLocation(int atIndex) {
        var boundingArea = BoundingArea.at(
                Point.at(fromDegrees(NORTH_EAST_LATS[atIndex]), fromDegrees(NORTH_EAST_LONS[atIndex])),
                Point.at(fromDegrees(SOUTH_WEST_LATS[atIndex]), fromDegrees(SOUTH_WEST_LONS[atIndex])));
        return new Location(
                POINT_NAMES[atIndex],
                Point.at(fromDegrees(CENTER_LATS[atIndex]), fromDegrees(CENTER_LONS[atIndex])),
                boundingArea);
    }
}