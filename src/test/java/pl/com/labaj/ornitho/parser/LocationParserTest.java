package pl.com.labaj.ornitho.parser;

import com.grum.geocalc.BoundingArea;
import com.grum.geocalc.Coordinate;
import com.grum.geocalc.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.com.labaj.ornitho.model.Location;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.com.labaj.ornitho.util.HtmlReader.readHtmlFromFile;

class LocationParserTest {

    private LocationParser locationParser;

    private static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of(
                        "ornitho/location/mielec.html",
                        new Location("Mielec [F8D4R3]",
                                Point.at(Coordinate.fromDegrees(50.29441111111111), Coordinate.fromDegrees(21.422641666666667)),
                                BoundingArea.at(
                                        Point.at(Coordinate.fromDegrees(50.29890749956795), Coordinate.fromDegrees(21.429680997045164)),
                                        Point.at(Coordinate.fromDegrees(50.289914297674), Coordinate.fromDegrees(21.415603666757065))))),
                Arguments.of(
                        "ornitho/location/krakow.html",
                        new Location("Kraków [E8N1F1]",
                                Point.at(Coordinate.fromDegrees(50.09166388888889), Coordinate.fromDegrees(19.84604722222222)),
                                BoundingArea.at(
                                        Point.at(Coordinate.fromDegrees(50.09616027886941), Coordinate.fromDegrees(19.853056721314495)),
                                        Point.at(Coordinate.fromDegrees(50.08716707697489), Coordinate.fromDegrees(19.83903903846342)))))
        );
    }

    @BeforeEach
    void setUp() {
        var positionParser = new PositionParser();
        locationParser = new LocationParser(positionParser);
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("testData")
    void shouldParseLocation(String locationPage, Location expectedLocation) {
        //given
        var pageHtml = readHtmlFromFile(locationPage);

        //when
        var location = locationParser.parse(pageHtml);

        //then
        assertThat(location).usingRecursiveComparison().isEqualTo(expectedLocation);
    }
}