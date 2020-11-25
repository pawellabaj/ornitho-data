package pl.com.labaj.ornitho.io;

import org.jsoup.Connection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PageLoaderTest {
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    @Mock
    PageLoader pageLoader;

    @Captor
    ArgumentCaptor<Connection> connectionCaptor;

    @BeforeAll
    static void init() {
        System.setProperty("login", LOGIN);
        System.setProperty("pwd", PASSWORD);
    }

    @Test
    void shouldSetAuthenticationCookies() {
        //given
        when(pageLoader.load(anyString())).thenCallRealMethod();
        var url = "http://ornitho.pl";

        //when
        pageLoader.load(url);

        //then
        verify(pageLoader).getDocumentFromConnection(connectionCaptor.capture());
        var connection = connectionCaptor.getValue();

        assertSoftly(softly -> {
            softly.assertThat(connection.request()).isNotNull();
            softly.assertThat(connection.request().url()).hasToString(url);
            softly.assertThat(connection.request().cookie("login_email")).isEqualTo(LOGIN);
            softly.assertThat(connection.request().cookie("login_password")).isEqualTo(PASSWORD);
        });
    }
}