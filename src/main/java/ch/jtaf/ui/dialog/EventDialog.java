package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.model.EventType;
import ch.jtaf.model.Gender;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToDoubleConverter;

public class EventDialog extends EditDialog<EventRecord> {

    public EventDialog(String title) {
        super(title);
    }

    @Override
    public void createForm() {
        TextField abbreviation = new TextField(getTranslation("Abbreviation"));
        abbreviation.setRequiredIndicatorVisible(true);
        formLayout.add(abbreviation);

        binder.forField(abbreviation)
                .withValidator(notEmptyValidator())
                .bind(EventRecord::getAbbreviation, EventRecord::setAbbreviation);

        TextField name = new TextField(getTranslation("Name"));
        name.setRequiredIndicatorVisible(true);
        formLayout.add(name);

        binder.forField(name)
                .withValidator(notEmptyValidator())
                .bind(EventRecord::getName, EventRecord::setName);

        Select<String> gender = new Select<>(getTranslation("Gender"));
        gender.setRequiredIndicatorVisible(true);
        gender.setItems(Gender.valuesAsStrings());
        formLayout.add(gender);

        binder.forField(gender)
                .bind(EventRecord::getGender, EventRecord::setGender);

        Select<String> eventType = new Select<>(getTranslation("Event.Type"));
        eventType.setRequiredIndicatorVisible(true);
        eventType.setItems(EventType.valuesAsStrings());
        formLayout.add(eventType);

        binder.forField(eventType)
                .withValidator(notEmptyValidator())
                .bind(EventRecord::getEventType, EventRecord::setEventType);

        TextField a = new TextField("A");
        a.setRequiredIndicatorVisible(true);
        formLayout.add(a);

        binder.forField(a)
                .withConverter(new StringToDoubleConverter(getTranslation("Must.be.a.number")))
                .bind(EventRecord::getA, EventRecord::setA);

        TextField b = new TextField("B");
        b.setRequiredIndicatorVisible(true);
        formLayout.add(b);

        binder.forField(b)
                .withConverter(new StringToDoubleConverter(getTranslation("Must.be.a.number")))
                .bind(EventRecord::getB, EventRecord::setB);

        TextField c = new TextField("C");
        c.setRequiredIndicatorVisible(true);
        formLayout.add(c);

        binder.forField(c)
                .withConverter(new StringToDoubleConverter(getTranslation("Must.be.a.number")))
                .bind(EventRecord::getC, EventRecord::setC);
    }
}
