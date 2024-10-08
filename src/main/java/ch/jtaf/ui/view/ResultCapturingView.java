package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.db.tables.records.ResultRecord;
import ch.jtaf.service.ResultCalculator;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serial;
import java.util.List;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Result.RESULT;
import static org.jooq.impl.DSL.upper;

@RolesAllowed({Role.USER, Role.ADMIN})
@Route(layout = MainLayout.class)
public class ResultCapturingView extends VerticalLayout implements HasDynamicTitle, HasUrlParameter<String> {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final String REMOVE_RESULTS = "Remove.results";

    private final Grid<Record4<Long, String, String, Long>> grid = new Grid<>();
    private final ConfigurableFilterDataProvider<Record4<Long, String, String, Long>, Void, String> dataProvider;
    private final Div form = new Div();
    private final transient DSLContext dsl;
    private final TransactionTemplate transactionTemplate;
    private final ResultCalculator resultCalculator;
    private TextField resultTextField;
    private long competitionId;

    public ResultCapturingView(DSLContext dsl, TransactionTemplate transactionTemplate, ResultCalculator resultCalculator) {
        this.dsl = dsl;
        this.transactionTemplate = transactionTemplate;
        this.resultCalculator = resultCalculator;

        this.dataProvider = new CallbackDataProvider<>(
            query -> {
                var athletes = getAthletes(dsl, query);
                if (athletes.size() == 1) {
                    grid.select(athletes.getFirst());
                    if (resultTextField != null) {
                        resultTextField.focus();
                    }
                }
                return athletes.stream();
            },
            (Query<Record4<Long, String, String, Long>, String> query) -> {
                int count = countAthletes(query);
                if (count == 0) {
                    form.removeAll();
                }
                return count;
            },
            athleteRecord -> athleteRecord.get(ATHLETE.ID)
        ).withConfigurableFilter();

        var filter = new TextField();
        filter.setId("filter");
        filter.setAutoselect(true);
        filter.setAutofocus(true);
        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        add(filter);

        grid.addColumn(athleteRecord -> athleteRecord.get(ATHLETE.ID)).setHeader("ID").setSortable(true).setAutoWidth(true).setKey(ATHLETE.ID.getName());
        grid.addColumn(athleteRecord -> athleteRecord.get(ATHLETE.LAST_NAME)).setHeader(getTranslation("Last.Name")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.LAST_NAME.getName());
        grid.addColumn(athleteRecord -> athleteRecord.get(ATHLETE.FIRST_NAME)).setHeader(getTranslation("First.Name")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.FIRST_NAME.getName());
        grid.setItems(dataProvider);
        grid.setHeight("200px");
        add(grid);

        add(form);

        grid.asSingleSelect().addValueChangeListener(this::createForm);
    }

    private void createForm(AbstractField.ComponentValueChangeEvent<Grid<Record4<Long, String, String, Long>>, Record4<Long, String, String, Long>> event) {
        form.removeAll();

        if (event.getValue() != null) {
            var formLayout = new FormLayout();
            form.add(formLayout);

            var events = getEvents(event);

            boolean first = true;
            int position = 0;
            for (var eventRecord : events) {
                var result = new TextField(eventRecord.getName());
                result.setId("result-" + position);
                formLayout.add(result);

                if (first) {
                    this.resultTextField = result;
                    first = false;
                }

                var points = new TextField();
                points.setId("points-" + position);
                points.setReadOnly(true);
                points.setEnabled(false);
                formLayout.add(points);

                var resultRecord = getResults(event, eventRecord);

                if (resultRecord != null) {
                    result.setValue(resultRecord.getResult());
                    points.setValue(resultRecord.getPoints() == null ? "" : resultRecord.getPoints().toString());
                } else {
                    resultRecord = RESULT.newRecord();
                    resultRecord.setPosition(position);
                    resultRecord.setEventId(eventRecord.getId());
                    resultRecord.setAthleteId(event.getValue().get(ATHLETE.ID));
                    resultRecord.setCategoryId(event.getValue().get(CATEGORY.ID));
                    resultRecord.setCompetitionId(competitionId);
                }

                var finalResultRecord = resultRecord;
                result.addValueChangeListener(ve ->
                    transactionTemplate.executeWithoutResult(ts -> {
                        var resultValue = ve.getValue();
                        finalResultRecord.setResult(resultValue);
                        finalResultRecord.setPoints(resultCalculator.calculatePoints(eventRecord, resultValue));
                        points.setValue(finalResultRecord.getPoints() == null ? "" : finalResultRecord.getPoints().toString());

                        dsl.attach(finalResultRecord);
                        finalResultRecord.store();
                    }));
                position++;
            }

            var dnf = new Checkbox(getTranslation("Dnf"));
            dnf.addValueChangeListener(e ->
                transactionTemplate.executeWithoutResult(t -> {
                    int updatedRows = dsl.update(CATEGORY_ATHLETE)
                        .set(CATEGORY_ATHLETE.DNF, e.getValue())
                        .where(CATEGORY_ATHLETE.ATHLETE_ID.eq(event.getValue().get(ATHLETE.ID)))
                        .and(CATEGORY_ATHLETE.CATEGORY_ID.eq(event.getValue().get(CATEGORY.ID)))
                        .execute();
                    if (updatedRows != 1) {
                        Notification.show(getTranslation("Set.dnf.unsuccessful"), 6000, Notification.Position.TOP_END);
                        t.setRollbackOnly();
                    }
                }));

            dsl.selectFrom(CATEGORY_ATHLETE)
                .where(CATEGORY_ATHLETE.ATHLETE_ID.eq(event.getValue().get(ATHLETE.ID)))
                .and(CATEGORY_ATHLETE.CATEGORY_ID.eq(event.getValue().get(CATEGORY.ID)))
                .fetchOptional()
                .ifPresent(categoryAthleteRecord -> dnf.setValue(categoryAthleteRecord.getDnf()));

            form.add(dnf);

            var removeResults = new Button(getTranslation(REMOVE_RESULTS));
            removeResults.addClassName(Margin.Top.MEDIUM);
            removeResults.addClickListener(e ->
                new ConfirmDialog("remove-results",
                    getTranslation(REMOVE_RESULTS),
                    getTranslation(REMOVE_RESULTS),
                    getTranslation("Confirm"),
                    ev -> transactionTemplate.executeWithoutResult(status -> {
                        dnf.setValue(false);

                        dsl.deleteFrom(RESULT)
                            .where(RESULT.ATHLETE_ID.eq(event.getValue().get(ATHLETE.ID)))
                            .and(RESULT.COMPETITION_ID.eq(competitionId))
                            .execute();

                        createForm(event);
                    }),
                    getTranslation("Cancel"),
                    ev -> {
                    }).open());
            form.add(removeResults);
        }
    }

    private int countAthletes(Query<Record4<Long, String, String, Long>, String> query) {
        var count = dsl
            .selectCount()
            .from(ATHLETE)
            .join(CATEGORY_ATHLETE).on(CATEGORY_ATHLETE.ATHLETE_ID.eq(ATHLETE.ID))
            .join(CATEGORY).on(CATEGORY.ID.eq(CATEGORY_ATHLETE.CATEGORY_ID))
            .join(COMPETITION).on(COMPETITION.SERIES_ID.eq(CATEGORY.SERIES_ID))
            .where(COMPETITION.ID.eq(competitionId).and(createCondition(query)))
            .and(CATEGORY.SERIES_ID.eq(COMPETITION.SERIES_ID))
            .fetchOneInto(Integer.class);
        return count != null ? count : 0;
    }

    private Result<Record4<Long, String, String, Long>> getAthletes(DSLContext dsl, Query<Record4<Long, String, String, Long>, String> query) {
        return dsl
            .select(
                ATHLETE.ID,
                ATHLETE.LAST_NAME,
                ATHLETE.FIRST_NAME,
                CATEGORY.ID)
            .from(ATHLETE)
            .join(CATEGORY_ATHLETE).on(CATEGORY_ATHLETE.ATHLETE_ID.eq(ATHLETE.ID))
            .join(CATEGORY).on(CATEGORY.ID.eq(CATEGORY_ATHLETE.CATEGORY_ID))
            .join(COMPETITION).on(COMPETITION.SERIES_ID.eq(CATEGORY.SERIES_ID))
            .where(COMPETITION.ID.eq(competitionId).and(createCondition(query)))
            .and(CATEGORY.SERIES_ID.eq(COMPETITION.SERIES_ID))
            .orderBy(ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME)
            .offset(query.getOffset()).limit(query.getLimit())
            .fetch();
    }

    private List<EventRecord> getEvents(AbstractField.ComponentValueChangeEvent<Grid<Record4<Long, String, String, Long>>, Record4<Long, String, String, Long>> event) {
        return dsl
            .select(CATEGORY_EVENT.event().fields())
            .from(CATEGORY_EVENT)
            .where(CATEGORY_EVENT.CATEGORY_ID.eq(event.getValue().get(CATEGORY.ID)))
            .orderBy(CATEGORY_EVENT.POSITION)
            .fetchInto(EventRecord.class);
    }

    private ResultRecord getResults(AbstractField.ComponentValueChangeEvent<Grid<Record4<Long, String, String, Long>>, Record4<Long, String, String, Long>> event, EventRecord eventRecord) {
        return dsl
            .selectFrom(RESULT)
            .where(RESULT.COMPETITION_ID.eq(competitionId))
            .and(RESULT.ATHLETE_ID.eq(event.getValue().get(ATHLETE.ID)))
            .and(RESULT.CATEGORY_ID.eq(event.getValue().get(CATEGORY.ID)))
            .and(RESULT.EVENT_ID.eq(eventRecord.getId()))
            .fetchOne();
    }

    @SuppressWarnings("DuplicatedCode")
    private Condition createCondition(Query<?, ?> query) {
        var optionalFilter = query.getFilter();
        if (optionalFilter.isPresent()) {
            String filterString = (String) optionalFilter.get();
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

    @Override
    public String getPageTitle() {
        var competition = dsl
            .select(COMPETITION.series().NAME, COMPETITION.NAME)
            .from(COMPETITION)
            .where(COMPETITION.ID.eq(competitionId))
            .fetchOne();
        if (competition == null) {
            return getTranslation("Enter.Results");
        } else {
            return "%s | %s - %s".formatted(
                getTranslation("Enter.Results"),
                competition.get(COMPETITION.series().NAME),
                competition.get(COMPETITION.NAME));
        }
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        competitionId = Long.parseLong(parameter);
        dataProvider.refreshAll();
    }

}
