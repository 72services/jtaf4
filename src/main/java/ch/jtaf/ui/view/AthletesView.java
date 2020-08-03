package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.ui.dialog.AthleteDialog;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

@Route(layout = MainLayout.class)
public class AthletesView extends ProtectedGridView<AthleteRecord> {

    private static final long serialVersionUID = 1L;

    private Map<Long, ClubRecord> clubRecordMap = new HashMap<>();

    public AthletesView(DSLContext dsl) {
        super(dsl, ATHLETE);

        setHeightFull();

        add(new H1(getTranslation("Athletes")));

        AthleteDialog dialog = new AthleteDialog(getTranslation("Athlete"));

        TextField filter = new TextField(getTranslation("Filter"));
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        add(filter);

        grid.addColumn(AthleteRecord::getLastName).setHeader(getTranslation("Last.Name")).setSortable(true);
        grid.addColumn(AthleteRecord::getFirstName).setHeader(getTranslation("First.Name")).setSortable(true);
        grid.addColumn(AthleteRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true);
        grid.addColumn(AthleteRecord::getYearOfBirth).setHeader(getTranslation("Year")).setSortable(true);
        grid.addColumn(athleteRecord -> athleteRecord.getClubId() == null ? null
            : clubRecordMap.get(athleteRecord.getClubId()).getAbbreviation()).setHeader(getTranslation("Club"));

        addActionColumnAndSetSelectionListener(grid, dialog, dataProvider::refreshAll, () -> {
            AthleteRecord newRecord = ATHLETE.newRecord();
            newRecord.setOrganizationId(organizationRecord.getId());
            return newRecord;
        });

        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        add(grid);

        filter.focus();
    }

    @Override
    protected void refreshAll() {
        super.refreshAll();
        var clubs = dsl.selectFrom(CLUB).where(CLUB.ORGANIZATION_ID.eq(organizationRecord.getId())).fetch();
        clubRecordMap = clubs.stream().collect(Collectors.toMap(ClubRecord::getId, clubRecord -> clubRecord));
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Athletes");
    }

    @Override
    protected Condition initialCondition() {
        return ATHLETE.ORGANIZATION_ID.eq(organizationRecord.getId());
    }

    @Override
    protected SortField<?>[] initialSort() {
        return new SortField[]{ATHLETE.GENDER.asc(), ATHLETE.YEAR_OF_BIRTH.asc(), ATHLETE.LAST_NAME.asc(),
            ATHLETE.FIRST_NAME.asc()};
    }
}
