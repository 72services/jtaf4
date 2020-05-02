package ch.jtaf.ui.translation;

import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Component
public class TranslationProvider implements I18NProvider {

    public static final String BUNDLE_PREFIX = "messages";

    public final Locale LOCALE_DE = new Locale("de", "CH");
    public final Locale LOCALE_EN = new Locale("en", "US");

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationProvider.class);

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(LOCALE_DE, LOCALE_EN);
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            LOGGER.warn("Got lang request for key with null value!");
            return "";
        }
        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PREFIX, locale);
        try {
            String value = bundle.getString(key);
            if (params.length > 0) {
                value = MessageFormat.format(value, params);
            }
            return value;
        } catch (final MissingResourceException e) {
            LOGGER.warn("Missing resource", e);
            return "!" + locale.getLanguage() + ": " + key;
        }
    }
}
