package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.component.JooqDataProviderProducer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.theme.lumo.Lumo;
import org.jooq.DSLContext;
import org.jooq.Field;

import java.util.Map;
import java.util.stream.Collectors;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

public class SearchAthleteDialog extends Dialog {

    public String FULLSCREEN = "fullscreen";

    private boolean isFullScreen = false;
    private Header header;
    private final Div content;
    private Button max;

    private final Map<Long, ClubRecord> clubRecordMap;
    private final ConfigurableFilterDataProvider<AthleteRecord, Void, String> dataProvider;
    private AthleteRecord selectedAthleteRecord;

    public SearchAthleteDialog(DSLContext dsl, OrganizationRecord organizationRecord) {
        setDraggable(true);
        setResizable(true);

        getElement().getThemeList().add("jtaf-dialog");
        getElement().setAttribute("aria-labelledby", "dialog-title");

        H2 headerTitel = new H2(getTranslation("Athletes"));
        headerTitel.addClassName("dialog-title");

        max = new Button(VaadinIcon.EXPAND_SQUARE.create());
        max.addClickListener(event -> maximise());

        Button close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        header = new Header(headerTitel, max, close);
        header.getElement().getThemeList().add(Lumo.LIGHT);
        add(header);

        AthleteDialog dialog = new AthleteDialog(getTranslation("Athlete"));

        TextField filter = new TextField(getTranslation("Filter"));
        filter.setValueChangeMode(ValueChangeMode.EAGER);

        var clubs = dsl.selectFrom(CLUB).where(CLUB.ORGANIZATION_ID.eq(organizationRecord.getId())).fetch();
        clubRecordMap = clubs.stream().collect(Collectors.toMap(ClubRecord::getId, clubRecord -> clubRecord));

        dataProvider = new JooqDataProviderProducer<>(
                dsl, ATHLETE,
                () -> ATHLETE.ORGANIZATION_ID.eq(organizationRecord.getId()),
                () -> new Field[]{ATHLETE.GENDER, ATHLETE.YEAR_OF_BIRTH, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME})
                .getDataProvider();

        Grid<AthleteRecord> grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        grid.getStyle().set("height", "calc(100% - 300px");

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

        content = new Div(filter, grid);
        content.addClassName("dialog-content");
        add(content);

        maximise();

        filter.focus();
    }

    public AthleteRecord getSelectedAthleteRecord() {
        return selectedAthleteRecord;
    }

    private void initialSize() {
        max.setIcon(VaadinIcon.EXPAND_SQUARE.create());
        getElement().getThemeList().remove(FULLSCREEN);
        setHeight("auto");
        setWidth("600px");
    }

    private void maximise() {
        if (isFullScreen) {
            initialSize();
        } else {
            max.setIcon(VaadinIcon.COMPRESS_SQUARE.create());
            getElement().getThemeList().add(FULLSCREEN);
            setSizeFull();
            content.setVisible(true);
        }
        isFullScreen = !isFullScreen;
    }

}
