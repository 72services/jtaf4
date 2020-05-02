package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;

public class SeriesDialog extends EditDialog<SeriesRecord> {

    public SeriesDialog(String title) {
        super(title);
    }

    @Override
    public void createForm() {
        TextField name = new TextField(getTranslation("Name"));
        name.setRequiredIndicatorVisible(true);
        formLayout.add(name);

        binder.forField(name)
                .withValidator(notEmptyValidator())
                .bind(SeriesRecord::getName, SeriesRecord::setName);
    }
}
