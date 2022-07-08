package ch.jtaf.ui.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;

public class ConfirmDialog extends Dialog {

    public ConfirmDialog(String id, String header, String text, String confirmText, Runnable confirmListener, String cancelText, Runnable cancelListener) {
        setId(id);

        setHeaderTitle(header);

        add(new Paragraph(text));

        var confirm = new Button(confirmText, event -> {
            confirmListener.run();
            close();
        });
        confirm.setId(id + "-confirm");
        confirm.getThemeNames().add("error");
        confirm.getThemeNames().add("primary");

        var cancel = new Button(cancelText, event -> {
            cancelListener.run();
            close();
        });
        cancel.setId(id + "-cancel");

        getFooter().add(confirm, cancel);
    }
}
