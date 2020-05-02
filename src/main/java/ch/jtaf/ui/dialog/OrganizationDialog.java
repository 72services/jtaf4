package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.OrganizationRecord;
import com.vaadin.flow.component.textfield.TextField;

public class OrganizationDialog extends EditDialog<OrganizationRecord> {

    public OrganizationDialog(String title) {
        super(title);
    }

    @Override
    public void createForm() {
        TextField key = new TextField("Key");
        key.setRequiredIndicatorVisible(true);
        formLayout.add(key);

        TextField name = new TextField("Name");
        name.setRequiredIndicatorVisible(true);
        formLayout.add(name);

        binder.forField(key)
                .withValidator(notEmptyValidator())
                .bind(OrganizationRecord::getOrganizationKey, OrganizationRecord::setOrganizationKey);

        binder.forField(name)
                .withValidator(notEmptyValidator())
                .bind(OrganizationRecord::getName, OrganizationRecord::setName);
    }

}
