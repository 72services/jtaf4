package ch.jtaf.ui.translation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TranslationProviderTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private TranslationProvider translationProvider;

    @Test
    void get_provided_locales() {
        var providedLocales = translationProvider.getProvidedLocales();

        assertThat(providedLocales).isNotEmpty().hasSize(2);
    }

    @Test
    void get_existing_translation() {
        var confirm = translationProvider.getTranslation("Confirm", Locale.ENGLISH);

        assertThat(confirm).isEqualTo("Confirm");
    }

    @Test
    void get_existing_translation_german() {
        var confirm = translationProvider.getTranslation("Confirm", Locale.GERMAN);

        assertThat(confirm).isEqualTo("Bestätigen");
    }

    @Test
    void get_missing_translation() {
        String hello = translationProvider.getTranslation("Hello", Locale.ENGLISH);

        assertThat(hello).isEqualTo("!en: Hello");
    }

    @Test
    void get_null_translation() {
        String nothing = translationProvider.getTranslation(null, Locale.ENGLISH);

        assertThat(nothing).isEmpty();
    }
}
