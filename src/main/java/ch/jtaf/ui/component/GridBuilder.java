package ch.jtaf.ui.component;

import ch.jtaf.ui.dialog.EditDialog;
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
                                                                                             Consumer<R> afterSave, Supplier<R> onNewRecord,
                                                                                             Runnable afterDelete) {
        addActionColumnAndSetSelectionListener(grid, dialog, afterSave, onNewRecord, null, null, afterDelete);
    }

    public static <R extends UpdatableRecord<R>> void addActionColumnAndSetSelectionListener(Grid<R> grid, EditDialog<R> dialog,
                                                                                             Consumer<R> afterSave, Supplier<R> onNewRecord,
                                                                                             String insteadOfDeleteTitle, Consumer<R> insteadOfDelete,
                                                                                             Runnable afterDelete) {
        var buttonAdd = new Button(grid.getTranslation("Add"));
        buttonAdd.setId("add-button");
        buttonAdd.addClickListener(event -> dialog.open(onNewRecord.get(), afterSave));
        grid.addComponentColumn(updatableRecord -> {
            var delete = new Button(insteadOfDeleteTitle != null ? insteadOfDeleteTitle : grid.getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event -> {
                if (insteadOfDelete != null) {
                    getBean(TransactionTemplate.class).executeWithoutResult(transactionStatus -> insteadOfDelete.accept(updatableRecord));
                } else {
                    ConfirmDialog confirmDialog = new ConfirmDialog(grid.getTranslation("Confirm"),
                        grid.getTranslation("Are.you.sure"),
                        grid.getTranslation("Delete"), e ->
                        getBean(TransactionTemplate.class).executeWithoutResult(transactionStatus -> {
                            try {
                                getBean(DSLContext.class).attach(updatableRecord);
                                updatableRecord.delete();

                                afterDelete.run();
                            } catch (DataAccessException ex) {
                                Notification.show(ex.getMessage());
                            }
                        }),
                        grid.getTranslation("Cancel"), e -> {
                    });
                    confirmDialog.setConfirmButtonTheme("error primary");
                    confirmDialog.setId("delete-confirm-dialog");
                    confirmDialog.open();
                }
            });

            var horizontalLayout = new HorizontalLayout(delete);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(buttonAdd).setKey("edit-column");

        grid.addItemClickListener(event -> dialog.open(event.getItem(), afterSave));
    }

}
