package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.model.EventType;
import ch.jtaf.model.Gender;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToDoubleConverter;

import java.io.Serial;

public class EventDialog extends EditDialog<EventRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MUST_BE_A_NUMBER = "Must.be.a.number";

    public EventDialog(String title) {
        super(title, "600px");
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void createForm() {
        TextField abbreviation = new TextField(getTranslation("Abbreviation"));
        abbreviation.setRequiredIndicatorVisible(true);

        binder.forField(abbreviation)
            .withValidator(new NotEmptyValidator(this))
            .bind(EventRecord::getAbbreviation, EventRecord::setAbbreviation);

        TextField name = new TextField(getTranslation("Name"));
        name.setRequiredIndicatorVisible(true);

        binder.forField(name)
            .withValidator(new NotEmptyValidator(this))
            .bind(EventRecord::getName, EventRecord::setName);

        Select<String> gender = new Select<>();
        gender.setLabel(getTranslation("Gender"));
        gender.setRequiredIndicatorVisible(true);
        gender.setItems(Gender.valuesAsStrings());

        binder.forField(gender)
            .bind(EventRecord::getGender, EventRecord::setGender);

        Select<String> eventType = new Select<>();
        eventType.setLabel(getTranslation("Event.Type"));
        eventType.setRequiredIndicatorVisible(true);
        eventType.setItems(EventType.valuesAsStrings());

        binder.forField(eventType)
            .withValidator(new NotEmptyValidator(this))
            .bind(EventRecord::getEventType, EventRecord::setEventType);

        TextField a = new TextField("A");
        a.setRequiredIndicatorVisible(true);

        binder.forField(a)
            .withConverter(new StringToDoubleConverter(getTranslation(MUST_BE_A_NUMBER)))
            .bind(EventRecord::getA, EventRecord::setA);

        TextField b = new TextField("B");
        b.setRequiredIndicatorVisible(true);

        binder.forField(b)
            .withConverter(new StringToDoubleConverter(getTranslation(MUST_BE_A_NUMBER)))
            .bind(EventRecord::getB, EventRecord::setB);

        TextField c = new TextField("C");
        c.setRequiredIndicatorVisible(true);

        binder.forField(c)
            .withConverter(new StringToDoubleConverter(getTranslation(MUST_BE_A_NUMBER)))
            .bind(EventRecord::getC, EventRecord::setC);

        formLayout.add(abbreviation, name, gender, eventType, a, b, c);
    }
}
