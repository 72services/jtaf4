package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.ui.dialog.EventDialog;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

import static ch.jtaf.db.tables.Event.EVENT;

@PageTitle("JTAF - Organizations")
@Route(layout = MainLayout.class)
public class EventsView extends ProtectedView {

    private final DSLContext dsl;
    private final Grid<EventRecord> grid;

    public EventsView(DSLContext dsl) {
        this.dsl = dsl;

        setHeightFull();

        add(new H1(getTranslation("Events")));

        Button add = new Button(getTranslation("Add.Series"));

        grid = new Grid<>();
        grid.setHeightFull();

        grid.addColumn(EventRecord::getName).setHeader(getTranslation("Name"));

        grid.addComponentColumn(eventRecord -> {
            Button delete = new Button(getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event -> {
                try {
                    dsl.attach(eventRecord);
                    eventRecord.delete();
                } catch (DataAccessException e) {
                    Notification.show(e.getMessage());
                }
            });

            HorizontalLayout horizontalLayout = new HorizontalLayout(delete);
            horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(add);

        EventDialog dialog = new EventDialog(getTranslation("Event"));

        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(eventRecord -> dialog.open(eventRecord, this::loadData)));

        add(grid);
    }

    @Override
    void loadData() {
        var events = dsl
                .selectFrom(EVENT)
                .where(EVENT.ORGANIZATION_ID.eq(organizationRecord.getId()))
                .fetch();

        grid.setItems(events);
    }

}
