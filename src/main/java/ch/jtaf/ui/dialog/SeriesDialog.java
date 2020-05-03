package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.SeriesRecord;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;

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

        Checkbox hidden = new Checkbox(getTranslation("Hidden"));
        formLayout.add(hidden);

        binder.forField(hidden)
                .bind(SeriesRecord::getHidden, SeriesRecord::setHidden);

        Checkbox locked = new Checkbox(getTranslation("Locked"));
        formLayout.add(locked);

        binder.forField(locked)
                .bind(SeriesRecord::getLocked, SeriesRecord::setLocked);

        // TODO Logo
    }
}
