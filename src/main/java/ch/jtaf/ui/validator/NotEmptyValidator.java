package ch.jtaf.ui.validator;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.validator.StringLengthValidator;

public class NotEmptyValidator extends StringLengthValidator {

    public NotEmptyValidator(Component component) {
        super(component.getTranslation("May.not.be.empty"), 1, null);
    }
}
