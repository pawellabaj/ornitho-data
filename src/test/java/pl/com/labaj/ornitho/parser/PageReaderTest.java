package pl.com.labaj.ornitho.parser;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.labaj.ornitho.io.PageLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageReaderTest {
    @Mock
    PageLoader pageLoader;

    @Mock
    DocumentParser<Object> documentParser;

    @InjectMocks
    PageReader<Object> pageReader;

    @Test
    void shouldLoadDocumentAndParseIt() {
        //given
        var url ="http://ornotho.pl";
        var document = mock(Document.class);
        var parsedArtifact = new Object();

        when(pageLoader.load(anyString())).thenReturn(document);
        when(documentParser.parse(any(Document.class))).thenReturn(parsedArtifact);

        //when
        var artifact = pageReader.read(url);

        //then
        verify(pageLoader).load(eq(url));
        verify(documentParser).parse(eq(document));

        assertThat(artifact).isEqualTo(parsedArtifact);
    }
}