package pl.com.labaj.ornitho.parser;

import com.grum.geocalc.Point;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pl.com.labaj.ornitho.model.Location;

import static com.grum.geocalc.EarthCalc.gcd.around;

@Slf4j
public class LocationParser implements DocumentParser<Location> {
    private static final String LOCATION_CELL_SELECTOR = "#td-main-table > tbody > tr > td > div.box > table > tbody > tr:nth-child(1) > td:nth-child(2)";
    private static final double DISTANCE = Math.sqrt(2) * 500;

    private final PositionParser positionParser;

    public LocationParser(PositionParser positionParser) {
        this.positionParser = positionParser;
    }

    @Override
    public Location parse(Document document) {
        var locationCell = document.select(LOCATION_CELL_SELECTOR);
        return buildLocation(locationCell.first());
    }

    private Location buildLocation(Element locationRow) {
        var name = locationRow.child(0).text();
        var center = parsePosition(locationRow.childNode(3).outerHtml());
        var boundingArea = around(center, DISTANCE);

        LOGGER.debug("Name: {}, Center: {}", name, center);

        return new Location(name, center, boundingArea);
    }

    private Point parsePosition(String position) {
        var coordinates = position.split("/");
        return positionParser.parse(coordinates[1], coordinates[0]);
    }
}
