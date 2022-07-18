package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.model.EventType;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.impl.DSL;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.security.RolesAllowed;
import java.io.Serial;

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

    private final Grid<Record4<Long, String, String, Long>> grid = new Grid<>();
    private final ConfigurableFilterDataProvider<Record4<Long, String, String, Long>, Void, String> dataProvider;
    private TextField resultTextField;

    private long competitionId;

    public ResultCapturingView(DSLContext dsl, TransactionTemplate transactionTemplate) {
        CallbackDataProvider<Record4<Long, String, String, Long>, String> callbackDataProvider = new CallbackDataProvider<>(
            query -> {
                var records = dsl
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
                if (records.size() == 1) {
                    grid.select(records.get(0));
                    if (resultTextField != null) {
                        resultTextField.focus();
                    }
                }
                return records.stream();
            },
            query -> {
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
            },
            athleteRecord -> athleteRecord.get(ATHLETE.ID)
        );
        dataProvider = callbackDataProvider.withConfigurableFilter();

        var filter = new TextField();
        filter.setId("filter");
        filter.focus();
        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        add(filter);

        grid.addColumn(athleteRecord -> athleteRecord.get(ATHLETE.ID)).setHeader("ID").setSortable(true).setAutoWidth(true).setKey(ATHLETE.ID.getName());
        grid.addColumn(athleteRecord -> athleteRecord.get(ATHLETE.LAST_NAME)).setHeader(getTranslation("Last.Name")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.LAST_NAME.getName());
        grid.addColumn(athleteRecord -> athleteRecord.get(ATHLETE.FIRST_NAME)).setHeader(getTranslation("First.Name")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.FIRST_NAME.getName());
        grid.setItems(dataProvider);
        grid.setHeight("200px");
        add(grid);

        var form = new Div();
        add(form);

        grid.asSingleSelect().addValueChangeListener(event -> {
            form.removeAll();

            if (event.getValue() != null) {
                var events = dsl
                    .select(CATEGORY_EVENT.event().fields())
                    .from(CATEGORY_EVENT)
                    .where(CATEGORY_EVENT.CATEGORY_ID.eq(event.getValue().get(CATEGORY.ID)))
                    .orderBy(CATEGORY_EVENT.POSITION)
                    .fetchInto(EventRecord.class);

                var formLayout = new FormLayout();
                form.add(formLayout);

                boolean first = true;
                int position = 0;
                for (var eventRecord : events) {
                    var resultRecord = dsl
                        .selectFrom(RESULT)
                        .where(RESULT.COMPETITION_ID.eq(competitionId))
                        .and(RESULT.ATHLETE_ID.eq(event.getValue().get(ATHLETE.ID)))
                        .and(RESULT.CATEGORY_ID.eq(event.getValue().get(CATEGORY.ID)))
                        .and(RESULT.EVENT_ID.eq(eventRecord.getId()))
                        .fetchOne();

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
                            finalResultRecord.setPoints(calculatePoints(eventRecord, resultValue));
                            points.setValue(finalResultRecord.getPoints() == null ? "" : finalResultRecord.getPoints().toString());

                            dsl.attach(finalResultRecord);
                            finalResultRecord.store();
                        }));
                    position++;
                }
            }
        });
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
        return getTranslation("Enter.Results");
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        competitionId = Long.parseLong(parameter);
        dataProvider.refreshAll();
    }

    private int calculatePoints(EventRecord event, String result) {
        double points = 0.0d;
        if (result != null) {
            if (EventType.valueOf(event.getEventType()) == EventType.RUN) {
                points = event.getA() * Math.pow((event.getB() - Double.parseDouble(result) * 100) / 100, event.getC());
            } else if (EventType.valueOf(event.getEventType()) == EventType.RUN_LONG) {
                String[] parts = result.split("\\.");
                double time;
                if (parts.length == 1) {
                    time = Double.parseDouble(parts[0]) * 60;
                } else {
                    time = Double.parseDouble(parts[0]) * 60 + Double.parseDouble(parts[1]);
                }
                points = event.getA() * Math.pow((event.getB() - time * 100) / 100, event.getC());
            } else if (EventType.valueOf(event.getEventType()) == EventType.JUMP_THROW) {
                points = event.getA() * Math.pow((Double.parseDouble(result) * 100 - event.getB()) / 100, event.getC());
            }
        }
        return (int) Math.round(points);
    }
}
