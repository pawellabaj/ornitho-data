package pl.com.labaj.ornitho.parser;

import com.grum.geocalc.Coordinate;
import com.grum.geocalc.Point;

import java.util.regex.Pattern;

import static com.grum.geocalc.Coordinate.fromDMS;
import static java.lang.Double.parseDouble;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.regex.Pattern.compile;

public class PositionParser {
    private static final Pattern COORDINATE_PATTERN = compile("(?<degrees>\\d{1,3})°((?<minutes>\\d{1,2})')?((?<seconds>\\d{1,2}\\.?\\d*)'')?\\s+(?<hemisphere>[NESW])");

     Point parse(String latitude, String longitude) {
         return Point.at(parseCoordinate(latitude), parseCoordinate(longitude));
    }

    private Coordinate parseCoordinate(String coordinate) {
        try {
            var matcher = COORDINATE_PATTERN.matcher(coordinate.trim());
            //noinspection ResultOfMethodCallIgnored
            matcher.find();

            var degrees = matcher.group("degrees");
            var minutes = matcher.group("minutes");
            var seconds = matcher.group("seconds");
            var hemisphere = matcher.group("hemisphere");

            var sign = sign(hemisphere);
          
            return fromDMS(parseOrZero(degrees) * sign,
                    parseOrZero(minutes),
                    parseOrZero(seconds));
        } catch (NumberFormatException | IllegalStateException e) {
            throw new OrnithoParserException(format("Cannot parse [%s]", coordinate), e);
        }
    }

    private double parseOrZero(String text) {
        return nonNull(text) ? parseDouble(text) : 0;
    }

    private int sign(String direction) {
        return "N".equals(direction) || "E".equals(direction) ? 1 : -1;
    }
}
