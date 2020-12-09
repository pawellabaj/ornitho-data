package pl.com.labaj.ornitho.parser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class SubjectParserTest {

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "puszczyk,puszczyk",
            "uszatka (Asio otus),uszatka",
            "family : Troglodytidae,Troglodytidae"
    })
    void shouldParseSubject(String text, String expectedSubject) {
        //given
        var subjectParser = new SubjectParser();

        //when
        var subject = subjectParser.parse(text);

        //then
        assertThat(subject).isEqualTo(expectedSubject);
    }
}