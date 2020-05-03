package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.dialog.AthleteDialog;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
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

@PageTitle("JTAF - Organizations")
@Route(layout = MainLayout.class)
public class AthletesView extends ProtectedView {

    private final DSLContext dsl;
    private final Grid<AthleteRecord> grid;

    public AthletesView(DSLContext dsl) {
        this.dsl = dsl;

        setHeightFull();

        add(new H1(getTranslation("Athletes")));

        AthleteDialog dialog = new AthleteDialog(getTranslation("Athlete"));

        Button add = new Button(getTranslation("Add.Athlete"));
        add.addClickListener(event -> dialog.open(ATHLETE.newRecord(), this::loadData));

        grid = new Grid<>();
        grid.setHeightFull();

        grid.addColumn(AthleteRecord::getFirstName).setHeader(getTranslation("First.Name"));
        grid.addColumn(AthleteRecord::getLastName).setHeader(getTranslation("Last.Name"));
        grid.addColumn(AthleteRecord::getGender).setHeader(getTranslation("Gender"));
        grid.addColumn(AthleteRecord::getYearOfBirth).setHeader(getTranslation("Year"));
        grid.addColumn(athleteRecord -> {
            OrganizationRecord organizationRecord = UI.getCurrent().getSession().getAttribute(OrganizationRecord.class);
            if (organizationRecord == null) {
                return null;
            } else {
                return dsl.select(CLUB.ABBREVIATION).from(CLUB)
                        .where(CLUB.ORGANIZATION_ID.eq(organizationRecord.getId())).and(CLUB.ID.eq(athleteRecord.getClubId()))
                        .fetchOneInto(String.class);
            }
        }).setHeader(getTranslation("Club"));

        grid.addComponentColumn(athleteRecord -> {
            Button delete = new Button(getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event -> {
                try {
                    dsl.attach(athleteRecord);
                    athleteRecord.delete();
                } catch (DataAccessException e) {
                    Notification.show(e.getMessage());
                }
            });

            HorizontalLayout horizontalLayout = new HorizontalLayout(delete);
            horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(add);

        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(athleteRecord -> dialog.open(athleteRecord, this::loadData)));

        add(grid);
    }

    @Override
    void loadData() {
        var athletes = dsl
                .selectFrom(ATHLETE)
                .where(ATHLETE.ORGANIZATION_ID.eq(organizationRecord.getId()))
                .fetch();

        grid.setItems(athletes);
    }

}
