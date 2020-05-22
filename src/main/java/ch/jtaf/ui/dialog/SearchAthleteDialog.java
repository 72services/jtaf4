package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.component.JooqDataProviderProducer;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.jooq.DSLContext;
import org.jooq.Field;

import java.util.Map;
import java.util.stream.Collectors;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

public class SearchAthleteDialog extends Dialog {

    private final Map<Long, ClubRecord> clubRecordMap;
    private final ConfigurableFilterDataProvider<AthleteRecord, Void, String> dataProvider;
    private AthleteRecord selectedAthleteRecord;

    public SearchAthleteDialog(DSLContext dsl, OrganizationRecord organizationRecord) {
        add(new H1(getTranslation("Athletes")));

        AthleteDialog dialog = new AthleteDialog(getTranslation("Athlete"));

        TextField filter = new TextField(getTranslation("Filter"));
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        add(filter);

        var clubs = dsl.selectFrom(CLUB).where(CLUB.ORGANIZATION_ID.eq(organizationRecord.getId())).fetch();
        clubRecordMap = clubs.stream().collect(Collectors.toMap(ClubRecord::getId, clubRecord -> clubRecord));

        dataProvider = new JooqDataProviderProducer<>(
                dsl, ATHLETE,
                () -> ATHLETE.ORGANIZATION_ID.eq(organizationRecord.getId()),
                () -> new Field[]{ATHLETE.GENDER, ATHLETE.YEAR_OF_BIRTH, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME})
                .getDataProvider();

        Grid<AthleteRecord> grid = new Grid<>();
        grid.addColumn(AthleteRecord::getLastName).setHeader(getTranslation("Last.Name")).setSortable(true);
        grid.addColumn(AthleteRecord::getFirstName).setHeader(getTranslation("First.Name")).setSortable(true);
        grid.addColumn(AthleteRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true);
        grid.addColumn(AthleteRecord::getYearOfBirth).setHeader(getTranslation("Year")).setSortable(true);
        grid.addColumn(athleteRecord -> athleteRecord.getClubId() == null ? null
                : clubRecordMap.get(athleteRecord.getClubId()).getAbbreviation()).setHeader(getTranslation("Club"));

        addActionColumnAndSetSelectionListener(grid, dialog, dataProvider::refreshAll,
                () -> {
                    AthleteRecord newRecord = ATHLETE.newRecord();
                    newRecord.setOrganizationId(organizationRecord.getId());
                    return newRecord;
                }, athleteRecord -> {
                    this.selectedAthleteRecord = athleteRecord;
                    this.close();
                });

        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        add(grid);

        filter.focus();

        setHeightFull();
        setWidthFull();
    }

    public AthleteRecord getSelectedAthleteRecord() {
        return selectedAthleteRecord;
    }
}
