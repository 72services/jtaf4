package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.OrganizationRecord;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;

public class OrganizationDialog extends EditDialog<OrganizationRecord> {

    @Override
    public void createForm() {
        TextField key = new TextField("Key");
        key.setRequiredIndicatorVisible(true);
        formLayout.add(key);

        TextField name = new TextField("Name");
        name.setRequiredIndicatorVisible(true);
        formLayout.add(name);

        binder.forField(key)
                .withValidator(new StringLengthValidator(
                        "Please set the key", 1, null))
                .bind(OrganizationRecord::getOrganizationKey, OrganizationRecord::setOrganizationKey);

        binder.forField(name)
                .withValidator(new StringLengthValidator(
                        "Please set the name", 1, null))
                .bind(OrganizationRecord::getName, OrganizationRecord::setName);
    }
}
