package ch.jtaf.ui.converter;

import com.vaadin.flow.data.binder.ErrorMessageProvider;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import java.text.NumberFormat;
import java.util.Locale;

public class JtafStringToIntegerConverter extends StringToIntegerConverter {


    public JtafStringToIntegerConverter(String errorMessage) {
        super(errorMessage);
    }

    public JtafStringToIntegerConverter(Integer emptyValue, String errorMessage) {
        super(emptyValue, errorMessage);
    }

    public JtafStringToIntegerConverter(ErrorMessageProvider errorMessageProvider) {
        super(errorMessageProvider);
    }

    public JtafStringToIntegerConverter(Integer emptyValue, ErrorMessageProvider errorMessageProvider) {
        super(emptyValue, errorMessageProvider);
    }

    protected java.text.NumberFormat getFormat(Locale locale) {
        NumberFormat format = super.getFormat(locale);
        format.setGroupingUsed(false);
        return format;
    }

}
