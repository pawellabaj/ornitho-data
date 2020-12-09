package pl.com.labaj.ornitho.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import pl.com.labaj.ornitho.model.Location;
import pl.com.labaj.ornitho.model.Observations;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
public class ObservationsParser implements DocumentParser<Observations> {

    private static final String INFO_ROWS_SELECTOR = "#td-main-table > tbody > tr > td > table > tbody > tr";
    private static final String LOCATION_LINKS_SELECTOR = "#td-main-table > tbody > tr > td > div.listContainer > div.listSubmenu > a";

    private final PageReader<Location> locationReader;
    private final SubjectParser subjectParser;
    private final DateRangeParser dateRangeParser;

    public ObservationsParser(PageReader<Location> locationReader, SubjectParser subjectParser, DateRangeParser dateRangeParser) {
        this.locationReader = locationReader;
        this.subjectParser = subjectParser;
        this.dateRangeParser = dateRangeParser;
    }

    @Override
    public Observations parse(Document document) {
        var infoRows = document.select(INFO_ROWS_SELECTOR);

        var subjectText = infoRows.get(1).child(1).text();
        var dateRangeText = infoRows.get(0).child(1).text();

        var subject = parseSubject(subjectText);
        var description = parseDescription(subjectText, dateRangeText);
        var dateRange = dateRangeParser.parse(dateRangeText);

        var locationLinks = document.select(LOCATION_LINKS_SELECTOR);
        var locations = parseLocations(locationLinks);
      
        LOGGER.info("{} locations for {}", locations.size(), subject);

        return new Observations(subject, description, dateRange, locations);
    }

    private String parseSubject(String subjectText) {
        return subjectParser.parse(subjectText);
    }

    private String parseDescription(String subjectText, String dateRangeText) {
        return  subjectText + "; " + dateRangeText;
    }

    private List<Location> parseLocations(Elements locationLinks) {
        return locationLinks.stream()
                .map(link -> link.attr("href"))
                .distinct()
                .map(locationReader::read)
                .collect(toList());
    }
}
