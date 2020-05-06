package ch.jtaf.ui.dialog;

import ch.jtaf.context.ApplicationContextHolder;
import ch.jtaf.ui.function.Callback;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.springframework.transaction.support.TransactionTemplate;

public abstract class EditDialog<R extends UpdatableRecord<?>> extends Dialog {

    final Binder<R> binder;
    final FormLayout formLayout;

    private Callback afterSave;
    private boolean initalized;

    public EditDialog(String title) {
        H3 h3Title = new H3(title);
        h3Title.getStyle().set("margin-top", "0px");
        add(h3Title);

        formLayout = new FormLayout();
        add(formLayout);

        binder = new Binder<>();

        Button save = new Button(getTranslation("Save"));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            DSLContext dsl = ApplicationContextHolder.getBean(DSLContext.class);
            TransactionTemplate transactionTemplate = ApplicationContextHolder.getBean(TransactionTemplate.class);
            transactionTemplate.executeWithoutResult((transactionStatus) -> {
                dsl.attach(binder.getBean());
                binder.getBean().store();

                if (afterSave != null) {
                    afterSave.execute();
                }
            });
            close();
        });

        Button cancel = new Button(getTranslation("Cancel"));
        cancel.addClickListener(event -> close());

        HorizontalLayout buttons = new HorizontalLayout(save, cancel);
        buttons.getStyle().set("padding-top", "20px");

        add(buttons);
    }

    public abstract void createForm();

    public void open(UpdatableRecord<?> record, Callback afterSave) {
        binder.setBean((R) record);
        this.afterSave = afterSave;

        if (!initalized) {
            createForm();
            initalized = true;
        }

        super.open();
    }

}
