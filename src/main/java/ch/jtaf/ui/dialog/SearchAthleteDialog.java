package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.theme.lumo.Lumo;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.Serial;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;
import static org.jooq.impl.DSL.upper;

public class SearchAthleteDialog extends Dialog {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String FULLSCREEN = "fullscreen";

    private boolean isFullScreen = false;
    private final Div content;
    private final Button max;

    private final Map<Long, ClubRecord> clubRecordMap;
    private final ConfigurableFilterDataProvider<AthleteRecord, Void, String> dataProvider;

    public SearchAthleteDialog(DSLContext dsl, Long organizationId, Long seriesId, Consumer<AthleteRecord> onSelect) {
        getElement().getThemeList().add("jtaf-dialog");
        getElement().setAttribute("aria-labelledby", "dialog-title");

        setDraggable(true);
        setResizable(true);

        H2 headerTitel = new H2(getTranslation("Athletes"));
        headerTitel.addClassName("dialog-title");

        max = new Button(VaadinIcon.EXPAND_SQUARE.create());
        max.addClickListener(event -> maximise());

        Button close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        Header header = new Header(headerTitel, max, close);
        header.getElement().getThemeList().add(Lumo.LIGHT);
        add(header);

        AthleteDialog dialog = new AthleteDialog(getTranslation("Athlete"));

        TextField filter = new TextField(getTranslation("Filter"));
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.focus();

        var clubs = dsl.selectFrom(CLUB).where(CLUB.ORGANIZATION_ID.eq(organizationId)).fetch();
        clubRecordMap = clubs.stream().collect(Collectors.toMap(ClubRecord::getId, clubRecord -> clubRecord));

        CallbackDataProvider<AthleteRecord, String> callbackDataProvider = DataProvider.fromFilteringCallbacks(
            query -> dsl
                .select(ATHLETE.fields())
                .from(ATHLETE)
                .where(ATHLETE.ORGANIZATION_ID.eq(organizationId))
                .and(ATHLETE.ID.notIn(dsl
                    .select(CATEGORY_ATHLETE.ATHLETE_ID)
                    .from(CATEGORY_ATHLETE)
                    .join(CATEGORY).on(CATEGORY.ID.eq(CATEGORY_ATHLETE.CATEGORY_ID))
                    .where(CATEGORY.SERIES_ID.eq(seriesId))
                ))
                .and(createCondition(query))
                .orderBy(ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME)
                .offset(query.getOffset()).limit(query.getLimit())
                .fetchStreamInto(ATHLETE),
            query -> {
                var count = dsl
                    .selectCount()
                    .from(ATHLETE)
                    .where(ATHLETE.ORGANIZATION_ID.eq(organizationId))
                    .and(ATHLETE.ID.notIn(dsl
                        .select(CATEGORY_ATHLETE.ATHLETE_ID)
                        .from(CATEGORY_ATHLETE)
                        .join(CATEGORY).on(CATEGORY.ID.eq(CATEGORY_ATHLETE.CATEGORY_ID))
                        .where(CATEGORY.SERIES_ID.eq(seriesId))
                    ))
                    .and(createCondition(query))
                    .fetchOneInto(Integer.class);
                return count != null ? count : 0;
            });

        dataProvider = callbackDataProvider.withConfigurableFilter();

        Grid<AthleteRecord> grid = new Grid<>();
        grid.setItems(dataProvider);
        grid.getStyle().set("height", "calc(100% - 300px");

        grid.addColumn(AthleteRecord::getLastName).setHeader(getTranslation("Last.Name")).setSortable(true);
        grid.addColumn(AthleteRecord::getFirstName).setHeader(getTranslation("First.Name")).setSortable(true);
        grid.addColumn(AthleteRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true);
        grid.addColumn(AthleteRecord::getYearOfBirth).setHeader(getTranslation("Year")).setSortable(true);
        grid.addColumn(athleteRecord -> athleteRecord.getClubId() == null ? null
            : clubRecordMap.get(athleteRecord.getClubId()).getAbbreviation()).setHeader(getTranslation("Club"));

        addActionColumnAndSetSelectionListener(grid, dialog, dataProvider::refreshAll, () -> {
            AthleteRecord newRecord = ATHLETE.newRecord();
            newRecord.setOrganizationId(organizationId);
            return newRecord;
        }, getTranslation("Assign.Athlete"), athleteRecord -> {
            onSelect.accept(athleteRecord);
            close();
        });

        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        content = new Div(filter, grid);
        content.addClassName("dialog-content");
        add(content);

        maximise();

        filter.focus();
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

    @SuppressWarnings("DuplicatedCode")
    private Condition createCondition(Query<?, ?> query) {
        if (query.getFilter().isPresent()) {
            String filterString = (String) query.getFilter().get();
            if (StringUtils.isNumeric(filterString)) {
                return ATHLETE.ID.eq(Long.valueOf(filterString));
            } else {
                return upper(ATHLETE.LAST_NAME).like(filterString.toUpperCase() + "%")
                    .or(upper(ATHLETE.FIRST_NAME).like(filterString.toUpperCase() + "%"));
            }
        } else {
            return DSL.condition("1 = 2");
        }
    }

}
