package pl.com.labaj.ornitho.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.com.labaj.ornitho.model.DateRange;

import java.time.LocalDate;
import java.util.stream.Stream;

import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;

class DateRangeParserTest {

    private static Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of(
                        "Od Środa 1 styczeń 2020 do czwartek 31 grudzień 2020",
                        new DateRange(LocalDate.of(2020, JANUARY, 1), LocalDate.of(2020, DECEMBER, 31))),
                Arguments.of(
                        "Od czwartek 5 grudzień 2019 do piątek 4 grudzień 2020",
                        new DateRange(LocalDate.of(2019, DECEMBER, 5), LocalDate.of(2020, DECEMBER, 4)))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("testData")
    void shouldParseDates(String text, DateRange expextedDateRange) {
        //given
        var dateRangeParser = new DateRangeParser();

        //when
        var dateRange = dateRangeParser.parse(text);

        //then
        assertThat(dateRange).isEqualTo(expextedDateRange);
    }
}