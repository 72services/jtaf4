package ch.jtaf.ui;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.function.Callback;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

@UIScope
@SpringComponent
public class OrganizationDialog extends Dialog {

    private final Binder<OrganizationRecord> binder;
    private Callback onSave;

    public OrganizationDialog(DSLContext dsl, TransactionTemplate transactionTemplate) {
        FormLayout formLayout = new FormLayout();
        binder = new Binder<>();

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

        add(formLayout);

        Button save = new Button("Save");
        save.addClickListener(event -> {
            transactionTemplate.executeWithoutResult((transactionStatus) -> {
                dsl.attach(binder.getBean());
                binder.getBean().store();

                if (onSave != null) {
                    onSave.execute();
                }
            });
            close();
        });

        Button cancel = new Button("Cancel");
        cancel.addClickListener(event -> close());

        add(new HorizontalLayout(save, cancel));
    }

    public void open(OrganizationRecord organizationRecord, Callback onSave) {
        binder.setBean(organizationRecord);
        this.onSave = onSave;

        super.open();
    }
}
