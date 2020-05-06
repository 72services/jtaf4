package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.ui.component.GridBuilder;
import ch.jtaf.ui.dialog.ClubDialog;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.ComponentUtil;
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

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

@PageTitle("JTAF - Clubs")
@Route(layout = MainLayout.class)
public class ClubsView extends ProtectedView {

    private final DSLContext dsl;
    private final Grid<ClubRecord> grid;

    public ClubsView(DSLContext dsl) {
        this.dsl = dsl;

        setHeightFull();

        add(new H1(getTranslation("Clubs")));

        ClubDialog dialog = new ClubDialog(getTranslation("Clubs"));

        Button add = new Button(getTranslation("Add.Club"));
        add.addClickListener(event -> {
            ClubRecord newRecord = CLUB.newRecord();
            newRecord.setOrganizationId(organizationRecord.getId());
            dialog.open(newRecord, this::loadData);
        });

        grid = new Grid<>();
        grid.setHeightFull();

        grid.addColumn(ClubRecord::getAbbreviation).setHeader(getTranslation("Abbreviation")).setSortable(true);
        grid.addColumn(ClubRecord::getName).setHeader(getTranslation("Name")).setSortable(true);

        addActionColumnAndSetSelectionListener(grid, dialog, this::loadData, () -> {
            ClubRecord newRecord = CLUB.newRecord();
            newRecord.setOrganizationId(organizationRecord.getId());
            return newRecord;
        });

        add(grid);
    }

    @Override
    void loadData() {
        var series = dsl
                .selectFrom(CLUB)
                .where(CLUB.ORGANIZATION_ID.eq(organizationRecord.getId()))
                .fetch();

        grid.setItems(series);
    }
}
