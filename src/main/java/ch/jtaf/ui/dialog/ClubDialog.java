package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.textfield.TextField;

public class ClubDialog extends EditDialog<ClubRecord> {

    public ClubDialog(String title) {
        super(title);
    }

    @Override
    public void createForm() {
        TextField abbreviation = new TextField(getTranslation("Abbreviation"));
        abbreviation.setRequiredIndicatorVisible(true);

        binder.forField(abbreviation)
                .withValidator(new NotEmptyValidator(this))
                .bind(ClubRecord::getAbbreviation, ClubRecord::setAbbreviation);

        TextField name = new TextField(getTranslation("Name"));
        name.setRequiredIndicatorVisible(true);

        binder.forField(name)
                .withValidator(new NotEmptyValidator(this))
                .bind(ClubRecord::getName, ClubRecord::setName);

        formLayout.add(abbreviation, name);
    }
}
