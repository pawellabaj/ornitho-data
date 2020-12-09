package pl.com.labaj.ornitho.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.com.labaj.ornitho.model.DateRange;
import pl.com.labaj.ornitho.model.Observations;

import java.nio.file.Path;
import java.time.LocalDate;

import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class PathGeneratorTest {

    @TempDir
    Path temporaryDirectory;

    private PathGenerator pathGenerator;

    @BeforeEach
    void setUp() {
        pathGenerator = new PathGenerator();
        pathGenerator.setContextPath(temporaryDirectory);
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "uszatka,uszatka-011119-311020.gpx",
            "puszczyk uralski,puszczyk_uralski-011119-311020.gpx"
    })
    void shouldGeneratePath(String subject, String expectedFileName) {
        //given
        var observations = new Observations(subject,
                "description",
                new DateRange(LocalDate.of(2019, NOVEMBER, 1), LocalDate.of(2020, OCTOBER, 31)),
                emptyList());

        //when
        var path = pathGenerator.getPath(observations, "gpx");

        //then
        assertSoftly(softly -> {
            softly.assertThat(path.getParent()).exists();
            softly.assertThat(path.getParent()).isDirectory();
            softly.assertThat(path.getParent()).hasFileName("output");

            softly.assertThat(path).hasFileName(expectedFileName);
        });
    }
}