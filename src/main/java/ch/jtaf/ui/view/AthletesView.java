package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.ui.dialog.AthleteDialog;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

@Route(layout = MainLayout.class)
public class AthletesView extends ProtectedGridView<AthleteRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    private Map<Long, ClubRecord> clubRecordMap = new HashMap<>();

    public AthletesView(DSLContext dsl, OrganizationProvider organizationProvider) {
        super(dsl, organizationProvider, ATHLETE);

        setHeightFull();

        var dialog = new AthleteDialog(getTranslation("Athlete"));

        var filter = new TextField(getTranslation("Filter"));
        filter.setId("filter");
        filter.setAutoselect(true);
        filter.setAutofocus(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        add(filter);

        grid.setId("athletes-grid");

        grid.addColumn(AthleteRecord::getLastName).setHeader(getTranslation("Last.Name")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.LAST_NAME.getName());
        grid.addColumn(AthleteRecord::getFirstName).setHeader(getTranslation("First.Name")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.FIRST_NAME.getName());
        grid.addColumn(AthleteRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.GENDER.getName());
        grid.addColumn(AthleteRecord::getYearOfBirth).setHeader(getTranslation("Year")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.YEAR_OF_BIRTH.getName());
        var c = grid.addColumn(athleteRecord -> athleteRecord.getClubId() == null ? null
            : clubRecordMap.get(athleteRecord.getClubId()).getAbbreviation()).setHeader(getTranslation("Club")).setAutoWidth(true);

        addActionColumnAndSetSelectionListener(grid, dialog, athleteRecord -> refreshAll(), () -> {
            AthleteRecord newRecord = ATHLETE.newRecord();
            newRecord.setOrganizationId(organizationRecord.getId());
            return newRecord;
        }, this::refreshAll);



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
