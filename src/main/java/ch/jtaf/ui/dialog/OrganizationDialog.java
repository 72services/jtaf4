package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.OrganizationRecord;
import com.vaadin.flow.component.textfield.TextField;

public class OrganizationDialog extends EditDialog<OrganizationRecord> {

    public OrganizationDialog(String title) {
        super(title);
    }

    @Override
    public void createForm() {
        TextField key = new TextField(getTranslation("Key"));
        key.setRequiredIndicatorVisible(true);
        formLayout.add(key);

        binder.forField(key)
                .withValidator(notEmptyValidator())
                .bind(OrganizationRecord::getOrganizationKey, OrganizationRecord::setOrganizationKey);

        TextField name = new TextField(getTranslation("Name"));
        name.setRequiredIndicatorVisible(true);
        formLayout.add(name);

        binder.forField(name)
                .withValidator(notEmptyValidator())
                .bind(OrganizationRecord::getName, OrganizationRecord::setName);
    }

}
