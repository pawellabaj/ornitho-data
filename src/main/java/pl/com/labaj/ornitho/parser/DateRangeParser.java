package pl.com.labaj.ornitho.parser;

import pl.com.labaj.ornitho.model.DateRange;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateRangeParser {
    private static final Locale POLISH_LOCALE = new Locale("pl", "PL");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dLLLLyyyy", POLISH_LOCALE);

    DateRange parse(String dateRangeText) {
        var periodElements = dateRangeText.split(" ");

        var from = LocalDate.parse(periodElements[2] + periodElements[3] + periodElements[4], DATE_TIME_FORMATTER);
        var to = LocalDate.parse(periodElements[7] + periodElements[8] + periodElements[9], DATE_TIME_FORMATTER);

        return new DateRange(from, to);
    }
}
