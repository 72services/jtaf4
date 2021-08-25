package ch.jtaf.ui.component;

import ch.jtaf.ui.dialog.EditDialog;
import ch.jtaf.ui.function.Callback;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static ch.jtaf.context.ApplicationContextHolder.getBean;

public class GridBuilder {

    private GridBuilder() {
    }

    public static <R extends UpdatableRecord<R>> void addActionColumnAndSetSelectionListener(Grid<R> grid, EditDialog<R> dialog,
                                                                                             Callback afterSave, Supplier<R> onNewRecord) {
        addActionColumnAndSetSelectionListener(grid, dialog, afterSave, onNewRecord, null, null);
    }

    public static <R extends UpdatableRecord<R>> void addActionColumnAndSetSelectionListener(Grid<R> grid, EditDialog<R> dialog,
                                                                                             Callback afterSave, Supplier<R> onNewRecord,
                                                                                             String insteadOfDeleteTitle, Consumer<R> insteadOfDelete) {
        Button buttonAdd = new Button(grid.getTranslation("Add"));
        buttonAdd.addClickListener(event -> dialog.open(onNewRecord.get(), afterSave));
        grid.addComponentColumn(record -> {
            Button delete = new Button(insteadOfDeleteTitle != null ? insteadOfDeleteTitle : grid.getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event -> {
                ConfirmDialog confirmDialog = new ConfirmDialog(grid.getTranslation("Confirm"),
                    grid.getTranslation("Are.you.sure"),
                    grid.getTranslation("Delete"), e ->
                    getBean(TransactionTemplate.class).executeWithoutResult(transactionStatus -> {
                        try {
                            if (insteadOfDelete != null) {
                                insteadOfDelete.accept(record);
                            } else {
                                getBean(DSLContext.class).attach(record);
                                record.delete();
                            }
                        } catch (DataAccessException ex) {
                            Notification.show(ex.getMessage());
                        }
                    }),
                    grid.getTranslation("Cancel"), e -> {
                });
                confirmDialog.setConfirmButtonTheme("error primary");
                confirmDialog.open();

            });
            HorizontalLayout horizontalLayout = new HorizontalLayout(delete);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(buttonAdd);
        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(
            record -> dialog.open(record, afterSave)));
    }

}
