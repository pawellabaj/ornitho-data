package pl.com.labaj.ornitho.grid.core;

import com.grum.geocalc.Point;

import java.util.regex.Pattern;

import static com.grum.geocalc.Coordinate.fromDegrees;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;

public class PositionParser {
    private static final Pattern PATTERN = compile("(?<degrees>\\d{1,3})°(?<minutes>\\d{1,2})'(?<seconds>\\d{1,2}\\.?\\d{0,2})''\\s+(?<direction>[NESW])\\s*");

    public Point parse(String[] coordinates) {
        double lonDegrees = parseDegrees(coordinates[0]);
        double latDegrees = parseDegrees(coordinates[1]);

        return Point.at(fromDegrees(latDegrees), fromDegrees(lonDegrees));
    }

    private double parseDegrees(String coordinate) {
        try {
            var matcher = PATTERN.matcher(coordinate);
            //noinspection ResultOfMethodCallIgnored
            matcher.find();

            var degrees = matcher.group("degrees");
            var minutes = matcher.group("minutes");
            var seconds = matcher.group("seconds");
            var direction = matcher.group("direction");

            return (parseInt(degrees) + parseInt(minutes) / 60f + parseDouble(seconds) / 3600f) * sign(direction);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Cannot parse [" + coordinate + "]", e);
        }
    }

    private int sign(String direction) {
        return "N".equals(direction) || "E".equals(direction) ? 1 : -1;
    }
}
