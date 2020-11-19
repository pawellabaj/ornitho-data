package pl.com.labaj.ornitho.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import pl.com.labaj.ornitho.model.DateRange;
import pl.com.labaj.ornitho.model.Location;
import pl.com.labaj.ornitho.model.Observations;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

@Slf4j
public class ObservationsParser implements DocumentParser<Observations> {

    private static final String INFO_ROWS_SELECTOR = "#td-main-table > tbody > tr > td > table > tbody > tr";
    private static final String LOCATION_LINKS_SELECTOR = "#td-main-table > tbody > tr > td > div.listContainer > div.listSubmenu > a";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dLLLLyyyy", new Locale("pl", "PL"));

    private final PageReader<Location> locationReader;
    private final SubjectParser subjectParser;

    public ObservationsParser(PageReader<Location> locationReader, SubjectParser subjectParser) {
        this.locationReader = locationReader;
        this.subjectParser = subjectParser;
    }

    public Observations parse(Document document) {
        var infoRows = document.select(INFO_ROWS_SELECTOR);

        var subjectText = infoRows.get(1).child(1).text();
        var dateRangeText = infoRows.get(0).child(1).text();

        var subject = parseSubject(subjectText);
        var description = parseDescription(subjectText, dateRangeText);
        var dateRange = parseDateRange(dateRangeText);

        var locationLinks = document.select(LOCATION_LINKS_SELECTOR);
        var locations = parseLocations(locationLinks);

        LOGGER.debug("{} locations parsed", locations.size());

        return new Observations(subject, description, dateRange, locations);
    }

    private String parseSubject(String subjectText) {
        return subjectParser.parseSubject(subjectText);
    }

    private String parseDescription(String subjectText, String dateRangeText) {
        return  subjectText + "; " + dateRangeText;
    }

    private DateRange parseDateRange(String dateRangeText) {
        var periodElements = dateRangeText.split(" ");

        var from = parseDate(periodElements[2] + periodElements[3] + periodElements[4]);
        var to = parseDate(periodElements[7] + periodElements[8] + periodElements[9]);

        return new DateRange(from, to);
    }

    private Date parseDate(String dateText) {
        try {
            return dateFormat.parse(dateText);
        } catch (ParseException e) {
            throw new OrnithoParserException(format("Cannot parse %s date", dateText), e);
        }
    }

    private List<Location> parseLocations(Elements locationLinks) {
        return locationLinks.stream()
                .map(link -> link.attr("href"))
                .distinct()
                .map(locationReader::read)
                .collect(toList());
    }
}
