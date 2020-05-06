package ch.jtaf.ui.view;

import ch.jtaf.db.tables.Club;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.dialog.ClubDialog;
import ch.jtaf.ui.dialog.SeriesDialog;
import ch.jtaf.ui.layout.MainLayout;
import ch.jtaf.util.LogoUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.db.tables.Series.SERIES;

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

        grid.addComponentColumn(clubRecord -> {
            Button delete = new Button(getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event -> {
                try {
                    dsl.attach(clubRecord);
                    clubRecord.delete();
                } catch (DataAccessException e) {
                    Notification.show(e.getMessage());
                }
            });

            HorizontalLayout horizontalLayout = new HorizontalLayout(delete);
            horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(add);

        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(seriesRecord -> dialog.open(seriesRecord, this::loadData)));

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
