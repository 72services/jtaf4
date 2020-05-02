package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.EventRecord;
import com.vaadin.flow.component.textfield.TextField;

public class EventDialog extends EditDialog<EventRecord> {

    public EventDialog(String title) {
        super(title);
    }

    @Override
    public void createForm() {
        TextField name = new TextField("Name");
        name.setRequiredIndicatorVisible(true);
        formLayout.add(name);

        binder.forField(name)
                .withValidator(notEmptyValidator())
                .bind(EventRecord::getName, EventRecord::setName);
    }
}
