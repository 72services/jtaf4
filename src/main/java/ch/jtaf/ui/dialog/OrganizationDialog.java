package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.textfield.TextField;

import java.io.Serial;

public class OrganizationDialog extends EditDialog<OrganizationRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    public OrganizationDialog(String title) {
        super(title, "600px");
    }

    @Override
    public void createForm() {
        TextField key = new TextField(getTranslation("Key"));
        key.setRequiredIndicatorVisible(true);
        formLayout.add(key);

        binder.forField(key)
            .withValidator(new NotEmptyValidator(this))
            .bind(OrganizationRecord::getOrganizationKey, OrganizationRecord::setOrganizationKey);

        TextField name = new TextField(getTranslation("Name"));
        name.setRequiredIndicatorVisible(true);
        formLayout.add(name);

        binder.forField(name)
            .withValidator(new NotEmptyValidator(this))
            .bind(OrganizationRecord::getName, OrganizationRecord::setName);
    }

}
