package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.ui.dialog.AthleteDialog;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

@PageTitle("JTAF - Organizations")
@Route(layout = MainLayout.class)
public class AthletesView extends ProtectedView {

    private final DSLContext dsl;
    private final Grid<AthleteRecord> grid;
    private Map<Long, ClubRecord> clubRecordMap = new HashMap<>();

    public AthletesView(DSLContext dsl) {
        this.dsl = dsl;

        setHeightFull();

        add(new H1(getTranslation("Athletes")));

        AthleteDialog dialog = new AthleteDialog(getTranslation("Athlete"));

        grid = new Grid<>();
        grid.setHeightFull();

        grid.addColumn(AthleteRecord::getLastName).setHeader(getTranslation("Last.Name")).setSortable(true);
        grid.addColumn(AthleteRecord::getFirstName).setHeader(getTranslation("First.Name")).setSortable(true);
        grid.addColumn(AthleteRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true);
        grid.addColumn(AthleteRecord::getYearOfBirth).setHeader(getTranslation("Year")).setSortable(true);
        grid.addColumn(athleteRecord -> athleteRecord.getClubId() == null ? null
                : clubRecordMap.get(athleteRecord.getClubId()).getAbbreviation()).setHeader(getTranslation("Club"));

        addActionColumnAndSetSelectionListener(grid, dialog, this::loadData, () -> {
            AthleteRecord newRecord = ATHLETE.newRecord();
            newRecord.setOrganizationId(organizationRecord.getId());
            return newRecord;
        });

        add(grid);
    }

    @Override
    void loadData() {
        var clubs = dsl.selectFrom(CLUB).where(CLUB.ORGANIZATION_ID.eq(organizationRecord.getId())).fetch();
        clubRecordMap = clubs.stream().collect(Collectors.toMap(ClubRecord::getId, clubRecord -> clubRecord));

        var athletes = dsl
                .selectFrom(ATHLETE)
                .where(ATHLETE.ORGANIZATION_ID.eq(organizationRecord.getId()))
                .orderBy(ATHLETE.GENDER, ATHLETE.YEAR_OF_BIRTH, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME)
                .fetch();

        grid.setItems(athletes);
    }

}
