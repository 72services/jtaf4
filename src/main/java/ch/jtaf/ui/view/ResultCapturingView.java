package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.db.tables.records.ResultRecord;
import ch.jtaf.model.EventType;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
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

@Route(layout = MainLayout.class)
public class ResultCapturingView extends VerticalLayout implements HasDynamicTitle, HasUrlParameter<String> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Grid<Record4<Long, String, String, Long>> grid = new Grid<>();
    private final ConfigurableFilterDataProvider<Record4<Long, String, String, Long>, Void, String> dataProvider;
    private TextField resultTextField;

    private long competitionId;

    public ResultCapturingView(DSLContext dsl, TransactionTemplate transactionTemplate) {
        add(new H1(getTranslation("Enter.Results")));

        CallbackDataProvider<Record4<Long, String, String, Long>, String> callbackDataProvider = new CallbackDataProvider<>(
            query -> {
                Result<Record4<Long, String, String, Long>> records = dsl
                    .select(
                        CATEGORY_ATHLETE.athlete().ID,
                        CATEGORY_ATHLETE.athlete().LAST_NAME,
                        CATEGORY_ATHLETE.athlete().FIRST_NAME,
                        CATEGORY_ATHLETE.category().ID)
                    .from(CATEGORY_ATHLETE)
                    .where(COMPETITION.ID.eq(competitionId).and(createCondition(query)))
                    .and(CATEGORY_ATHLETE.category().SERIES_ID.eq(COMPETITION.SERIES_ID))
                    .orderBy(CATEGORY_ATHLETE.athlete().LAST_NAME, CATEGORY_ATHLETE.athlete().FIRST_NAME)
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
                    .from(CATEGORY_ATHLETE)
                    .where(COMPETITION.ID.eq(competitionId).and(createCondition(query)))
                    .and(CATEGORY_ATHLETE.category().SERIES_ID.eq(COMPETITION.SERIES_ID))
                    .fetchOneInto(Integer.class);
                return count != null ? count : 0;
            },
            record -> record.get(ATHLETE.ID)
        );
        dataProvider = callbackDataProvider.withConfigurableFilter();

        TextField filter = new TextField();
        filter.focus();
        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        add(filter);

        grid.addColumn(record -> record.get(ATHLETE.ID)).setHeader("ID").setSortable(true);
        grid.addColumn(record -> record.get(ATHLETE.LAST_NAME)).setHeader(getTranslation("Last.Name")).setSortable(true);
        grid.addColumn(record -> record.get(ATHLETE.FIRST_NAME)).setHeader(getTranslation("First.Name")).setSortable(true);
        grid.setDataProvider(dataProvider);
        grid.setHeight("200px");
        add(grid);

        Div form = new Div();
        add(form);

        grid.asSingleSelect().addValueChangeListener(event -> {
            form.removeAll();

            if (event.getValue() != null) {
                List<EventRecord> events = dsl
                    .select(CATEGORY_EVENT.event().fields())
                    .from(CATEGORY_EVENT)
                    .where(CATEGORY_EVENT.CATEGORY_ID.eq(event.getValue().get(CATEGORY.ID)))
                    .orderBy(CATEGORY_EVENT.POSITION)
                    .fetchInto(EventRecord.class);

                FormLayout formLayout = new FormLayout();
                form.add(formLayout);

                boolean first = true;
                for (EventRecord eventRecord : events) {
                    ResultRecord resultRecord = dsl
                        .selectFrom(RESULT)
                        .where(RESULT.COMPETITION_ID.eq(competitionId))
                        .and(RESULT.ATHLETE_ID.eq(event.getValue().get(ATHLETE.ID)))
                        .and(RESULT.CATEGORY_ID.eq(event.getValue().get(CATEGORY.ID)))
                        .and(RESULT.EVENT_ID.eq(eventRecord.getId()))
                        .fetchOne();

                    TextField result = new TextField(eventRecord.getName());
                    if (resultRecord != null) {
                        result.setValue(resultRecord.getResult());
                        formLayout.add(result);

                        if (first) {
                            this.resultTextField = result;
                            first = false;
                        }

                        TextField points = new TextField();
                        points.setReadOnly(true);
                        points.setEnabled(false);
                        points.setValue(resultRecord.getPoints() == null ? "" : resultRecord.getPoints().toString());
                        formLayout.add(points);

                        result.addValueChangeListener(ve ->
                            transactionTemplate.executeWithoutResult(ts -> {
                                String resultValue = ve.getValue();
                                resultRecord.setResult(resultValue);
                                resultRecord.setPoints(calculatePoints(eventRecord, resultValue));
                                points.setValue(resultRecord.getPoints() == null ? "" : resultRecord.getPoints().toString());

                                resultRecord.store();
                            }));
                    }
                }
            }
        });
    }

    private Condition createCondition(Query<?, ?> query) {
        if (query.getFilter().isPresent()) {
            String filterString = (String) query.getFilter().get();
            if (StringUtils.isNumeric(filterString)) {
                return ATHLETE.ID.eq(Long.valueOf(filterString));
            } else {
                return ATHLETE.LAST_NAME.like(filterString + "%")
                    .or(ATHLETE.FIRST_NAME.like(filterString + "%"));
            }
        } else {
            return DSL.condition("1 = 2");
        }
    }

    @Override
    public String getPageTitle() {
        return "JTAF - " + getTranslation("Enter.Results");
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        if (parameter == null) {
            UI.getCurrent().navigate(DashboardView.class);
        } else {
            competitionId = Long.parseLong(parameter);

            dataProvider.refreshAll();
        }
    }

    private int calculatePoints(EventRecord event, String result) {
        double points = 0.0d;
        if (result != null && Double.parseDouble(result) > 0) {
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
