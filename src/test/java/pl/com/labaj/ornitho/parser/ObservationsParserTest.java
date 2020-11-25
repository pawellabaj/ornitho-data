package pl.com.labaj.ornitho.parser;

import com.grum.geocalc.BoundingArea;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.labaj.ornitho.model.DateRange;
import pl.com.labaj.ornitho.model.Location;
import pl.com.labaj.ornitho.model.Observations;
import pl.com.labaj.ornitho.util.HtmlReader;

import java.time.LocalDate;
import java.util.List;

import static java.time.Month.DECEMBER;
import static java.time.Month.SEPTEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservationsParserTest {

    private Location location;

    @Mock
    private PageReader<Location> locationReader;

    @Captor
    ArgumentCaptor<String> locationUrlCaptor;

    private ObservationsParser observationsParser;

    @BeforeEach
    void setUp() {
        var point = Point.at(Coordinate.fromDegrees(0), Coordinate.fromDegrees(0));
        location = new Location("dummy", point, BoundingArea.at(point, point));
        when(locationReader.read(anyString())).thenReturn(location);

        var subjectParser = new SubjectParser();
        var dateRangeParser = new DateRangeParser();
        observationsParser = new ObservationsParser(locationReader, subjectParser, dateRangeParser);
    }

    @Test
    void shouldParseObservationsFromSinglePage() {
        //given
        var pageHtml = HtmlReader.readHtmlFromFile("ornitho/observations/single-page.html");
        var expectedLocationUrls = List.of("https://www.ornitho.pl/index.php?m_id=52&id=98699", "https://www.ornitho.pl/index.php?m_id=52&id=138503");
        var expectedObservations = new Observations("uszatka",
                "uszatka (Asio otus); Od wtorek 1 wrzesień 2020 do piątek 4 grudzień 2020",
                new DateRange(LocalDate.of(2020, SEPTEMBER, 1), LocalDate.of(2020, DECEMBER, 4)),
                List.of(location, location));

        //when
        var observations = observationsParser.parse(pageHtml);

        //then
        verify(locationReader, times(2)).read(locationUrlCaptor.capture());
        assertThat(locationUrlCaptor.getAllValues()).containsExactlyInAnyOrderElementsOf(expectedLocationUrls);

        assertThat(observations).usingRecursiveComparison().isEqualTo(expectedObservations);
    }
}