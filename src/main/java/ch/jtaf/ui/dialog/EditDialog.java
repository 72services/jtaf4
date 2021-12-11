package ch.jtaf.ui.dialog;

import ch.jtaf.ui.function.Callback;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.theme.lumo.Lumo;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serial;

import static ch.jtaf.context.ApplicationContextHolder.getBean;

public abstract class EditDialog<R extends UpdatableRecord<?>> extends Dialog {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String FULLSCREEN = "fullscreen";

    private boolean isFullScreen = false;
    private final Div content;
    private final Button max;

    final Binder<R> binder;
    final FormLayout formLayout;

    private Callback afterSave;
    private boolean initialized;

    public EditDialog(String title) {
        getElement().getThemeList().add("jtaf-dialog");
        getElement().setAttribute("aria-labelledby", "dialog-title");

        setDraggable(true);
        setResizable(true);

        setWidth("600px");

        H2 headerTitel = new H2(title);
        headerTitel.addClassName("dialog-title");

        max = new Button(VaadinIcon.EXPAND_SQUARE.create());
        max.addClickListener(event -> maximise());

        Button close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        Header header = new Header(headerTitel, max, close);
        header.getElement().getThemeList().add(Lumo.LIGHT);
        add(header);

        formLayout = new FormLayout();

        binder = new Binder<>();

        Button save = new Button(getTranslation("Save"));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            getBean(TransactionTemplate.class).executeWithoutResult((transactionStatus) -> {
                getBean(DSLContext.class).attach(binder.getBean());
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

        content = new Div(formLayout, buttons);
        content.addClassName("dialog-content");

        add(content);
    }

    public abstract void createForm();

    @SuppressWarnings("unchecked")
    public void open(UpdatableRecord<?> record, Callback afterSave) {
        binder.setBean((R) record);
        this.afterSave = afterSave;

        if (!initialized) {
            createForm();
            initialized = true;
        }

        super.open();
    }

    private void initialSize() {
        max.setIcon(VaadinIcon.EXPAND_SQUARE.create());
        getElement().getThemeList().remove(FULLSCREEN);
        setHeight("auto");
        setWidth("600px");
    }

    private void maximise() {
        if (isFullScreen) {
            initialSize();
        } else {
            max.setIcon(VaadinIcon.COMPRESS_SQUARE.create());
            getElement().getThemeList().add(FULLSCREEN);
            setSizeFull();
            content.setVisible(true);
        }
        isFullScreen = !isFullScreen;
    }

}
