package pl.com.labaj.ornitho.io.gpx;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import pl.com.labaj.ornitho.io.DocumentLoader;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static pl.com.labaj.ornitho.util.DocumentBuilder.buildDocument;
import static pl.com.labaj.ornitho.util.DocumentTagNamesComparator.documentComparator;

@ExtendWith(MockitoExtension.class)
class GPXExtensionsTest {
    private static final Document WAYPOINT_EXTENSIONS = buildDocument("extensions", "wayPoint");
    private static final Document LINE_EXTENSIONS = buildDocument("extensions", "line");

    @Mock
    DocumentLoader documentLoader;

    @Test
    void shouldLoadGPXExtensions() {
        //given
        when(documentLoader.loadDocument(eq("gpx/way-point-extensions.xml"))).thenReturn(WAYPOINT_EXTENSIONS);
        when(documentLoader.loadDocument(eq("gpx/line-extensions.xml"))).thenReturn(LINE_EXTENSIONS);

        var gpxExtensions = new GPXExtensions(documentLoader);

        //when

        //then
        assertSoftly(softly -> {
            softly.assertThat(gpxExtensions.getLineExtensions()).usingComparator(documentComparator()).isEqualTo(LINE_EXTENSIONS);
            softly.assertThat(gpxExtensions.getWaypointExtensions()).usingComparator(documentComparator()).isEqualTo(WAYPOINT_EXTENSIONS);
        });
    }
}