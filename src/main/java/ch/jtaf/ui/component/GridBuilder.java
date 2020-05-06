package ch.jtaf.ui.component;

import ch.jtaf.ui.dialog.EditDialog;
import ch.jtaf.ui.function.Callback;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.jooq.DSLContext;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

import static ch.jtaf.context.ApplicationContextHolder.getBean;

public class GridBuilder {

    private GridBuilder() {
    }

    public static void addActionColumnAndSetSelectionListener(Grid<? extends UpdatableRecord<?>> grid, EditDialog<? extends UpdatableRecord<?>> dialog, Callback afterSave, Supplier<UpdatableRecord> onNewRecord) {
        Button buttonAdd = new Button(grid.getTranslation("Add"));
        buttonAdd.addClickListener(event -> dialog.open(onNewRecord.get(), afterSave));

        grid.addComponentColumn(record -> {
            Button delete = new Button(grid.getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event -> {
                getBean(TransactionTemplate.class).executeWithoutResult(transactionStatus -> {
                    try {
                        getBean(DSLContext.class).attach(record);
                        record.delete();
                    } catch (DataAccessException e) {
                        Notification.show(e.getMessage());
                    }
                });
            });

            HorizontalLayout horizontalLayout = new HorizontalLayout(delete);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(buttonAdd);

        grid.addSelectionListener(event -> event.getFirstSelectedItem().ifPresent(record -> dialog.open(record, afterSave)));
    }

}
