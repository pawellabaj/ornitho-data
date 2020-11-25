package pl.com.labaj.ornitho;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.labaj.ornitho.builder.gpx.GPXBuilder;
import pl.com.labaj.ornitho.io.PathGenerator;
import pl.com.labaj.ornitho.model.DateRange;
import pl.com.labaj.ornitho.model.Observations;
import pl.com.labaj.ornitho.parser.PageReader;

import java.nio.file.Path;
import java.time.LocalDate;

import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {

    @Mock
    PageReader<Observations> pageReader;

    @Mock
    PathGenerator pathGenerator;

    @Mock
    GPXBuilder gpxBuilder;

    @InjectMocks
    Application application;

    @Test
    void shouldRunApplication() {
        //given
        var url = "http://ornitho.pl";
        var observations = createObservations();
        var fileName = Path.of("output", "subject-011119-311020.gpx");

        when(pageReader.read(anyString())).thenReturn(observations);
        when(pathGenerator.getPath(any(Observations.class), anyString())).thenReturn(fileName);

        //when
        application.run(url);

        //then
        verify(pageReader).read(eq(url));
        verify(pathGenerator).getPath(eq(observations), eq("gpx"));
        verify(gpxBuilder).buildAndSave(eq(observations), eq(fileName));
    }

    private Observations createObservations() {
        return new Observations("subject",
                "description",
                new DateRange(LocalDate.of(2019, NOVEMBER, 1), LocalDate.of(2020, OCTOBER, 31)),
                emptyList());
    }
}