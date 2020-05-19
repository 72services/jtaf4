package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.ui.dialog.EventDialog;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;

import static ch.jtaf.db.tables.Event.EVENT;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

@PageTitle("JTAF - Organizations")
@Route(layout = MainLayout.class)
public class EventsView extends ProtectedView {

    private final DSLContext dsl;
    private final Grid<EventRecord> grid;

    public EventsView(DSLContext dsl) {
        this.dsl = dsl;

        setHeightFull();

        add(new H1(getTranslation("Events")));

        EventDialog dialog = new EventDialog(getTranslation("Event"));

        grid = new Grid<>();
        grid.setHeightFull();

        grid.addColumn(EventRecord::getAbbreviation).setHeader(getTranslation("Abbreviation")).setSortable(true);
        grid.addColumn(EventRecord::getName).setHeader(getTranslation("Name")).setSortable(true);
        grid.addColumn(EventRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true);
        grid.addColumn(EventRecord::getEventType).setHeader(getTranslation("Event.Type")).setSortable(true);
        grid.addColumn(EventRecord::getA).setHeader("A");
        grid.addColumn(EventRecord::getB).setHeader("B");
        grid.addColumn(EventRecord::getC).setHeader("C");

        addActionColumnAndSetSelectionListener(grid, dialog, this::loadData, () -> {
            EventRecord newRecord = EVENT.newRecord();
            newRecord.setOrganizationId(organizationRecord.getId());
            return newRecord;
        });

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
