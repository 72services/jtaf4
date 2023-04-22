package ch.jtaf.ui.translation;

import com.vaadin.flow.i18n.I18NProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Component
public class TranslationProvider implements I18NProvider {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TranslationProvider.class);

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(Locale.of("de", "CH"), Locale.of("en", "US"));
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            LOGGER.warn("Got lang request for key with null value!");
            return "";
        }
        final ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        try {
            var value = bundle.getString(key);
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
