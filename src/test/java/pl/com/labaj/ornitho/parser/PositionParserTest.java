package pl.com.labaj.ornitho.parser;

import com.grum.geocalc.Point;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.grum.geocalc.Coordinate.fromDegrees;
import static org.assertj.core.api.Assertions.assertThat;

class PositionParserTest {

    private static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of("50°12'03.15'' N", "20°24'14.4'' E", point(50.200875, 20.404)),
                Arguments.of("50°12' N", "20°24' E", point(50.2, 20.4)),
                Arguments.of("50° N", "20° E", point(50.0, 20.0)),
                Arguments.of("50°12'03.15'' S", "20°24'14.4'' W", point(-50.200875, -20.404)),
                Arguments.of("05°05'05.50'' N", "02°02'02.20'' E", point(5.084861111111111, 2.0339444444444443)),
                Arguments.of("5°5'5.5'' N", "2°2'2.2'' E", point(5.084861111111111, 2.0339444444444443))
        );
    }

    private static Point point(double lat, double lon) {
        return Point.at(fromDegrees(lat), fromDegrees(lon));
    }

    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("testData")
    void shouldParsePosition(String latitude, String longitude, Point point) {
        //given
        var positionParser = new PositionParser();

        //when
        var position = positionParser.parse(latitude, longitude);

        //then
        assertThat(position).isEqualTo(point);
    }
}