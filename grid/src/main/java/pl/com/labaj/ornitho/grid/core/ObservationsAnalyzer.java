package pl.com.labaj.ornitho.grid.core;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import pl.com.labaj.ornitho.grid.io.PageLoader;
import pl.com.labaj.ornitho.grid.model.DateRange;
import pl.com.labaj.ornitho.grid.model.Location;
import pl.com.labaj.ornitho.grid.model.Observations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ObservationsAnalyzer implements Analyzer<Observations> {

    private static final String SELECTOR_INFO_ROWS = "#td-main-table > tbody > tr > td > table > tbody > tr";
    private static final String SELECTOR_LOCATION_LINKS = "#td-main-table > tbody > tr > td > div.listContainer > div.listSubmenu > a";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dMMMMyyyy", new Locale("pl", "PL"));

    private final Analyzer<Location> locationAnalyzer;
    private final PageLoader pageLoader;

    public ObservationsAnalyzer(Analyzer<Location> locationAnalyzer, PageLoader pageLoader) {
        this.locationAnalyzer = locationAnalyzer;
        this.pageLoader = pageLoader;
    }

    @Override
    public Observations apply(Document document) {
        var infoRows = document.select(SELECTOR_INFO_ROWS);
        var locationLinks = document.select(SELECTOR_LOCATION_LINKS);

        var specie = getSpecie(infoRows);
        var dateRange = parseDateRange(infoRows);
        var locations = toLocations(locationLinks);

        LOGGER.debug("{} locations analyzed", locations.size());

        return new Observations(specie, dateRange, locations);
    }

    private String getSpecie(Elements infoRows) {
        var text = infoRows.get(1).child(1).text();
        if (text.contains(":")) {
            return text.split(":")[1].trim();
        }
        if (text.contains("(")) {
            return text.split("\\(")[0].trim();
        }
        return text;
    }

    private DateRange parseDateRange(Elements infoRows) {
        var period = infoRows.get(0).child(1).text();
        var periodElements = period.split(" ");

        try {
            var from = DATE_FORMAT.parse(periodElements[2] + periodElements[3] + periodElements[4]);
            var to = DATE_FORMAT.parse(periodElements[7] + periodElements[8] + periodElements[9]);

            return new DateRange(from, to);
        } catch (ParseException e) {
            throw new RuntimeException("Cannot parse the date", e);
        }
    }

    private List<Location> toLocations(Elements locationLinks) {
        return locationLinks.stream()
                .map(link -> link.attr("href"))
                .distinct()
                .map(pageLoader::load)
                .map(locationAnalyzer)
                .collect(toList());
    }
}
