package pl.com.labaj.ornitho.io;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.com.labaj.ornitho.util.DocumentBuilder.buildDocument;
import static pl.com.labaj.ornitho.util.DocumentTagNamesComparator.documentComparator;

class DocumentLoaderTest {

    @Test
    void shouldLoadDocument() {
        //given
        var documentLoader = new DocumentLoader();
        var expectedDocument = buildDocument("a", "b", "c");

        //when
        var document = documentLoader.loadDocument("test-document.xml");

        //then
        assertThat(document).usingComparator(documentComparator()).isEqualTo(expectedDocument);
    }
}