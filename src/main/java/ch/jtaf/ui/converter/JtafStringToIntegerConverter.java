package ch.jtaf.ui.converter;

import com.vaadin.flow.data.converter.StringToIntegerConverter;

import java.util.Locale;

public class JtafStringToIntegerConverter extends StringToIntegerConverter {

    public JtafStringToIntegerConverter(String errorMessage) {
        super(errorMessage);
    }

    @Override
    protected java.text.NumberFormat getFormat(Locale locale) {
        var format = super.getFormat(locale);
        format.setGroupingUsed(false);
        return format;
    }

}
