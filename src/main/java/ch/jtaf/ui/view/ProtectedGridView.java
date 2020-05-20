package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.security.OrganizationHolder;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import java.util.stream.Stream;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static org.jooq.impl.DSL.noCondition;
import static org.jooq.impl.DSL.upper;

public abstract class ProtectedGridView<R extends Record> extends ProtectedView {

    final Table<R> table;
    final ConfigurableFilterDataProvider<R, Void, String> dataProvider;
    final Grid<R> grid;

    public ProtectedGridView(DSLContext dsl, Table<R> table) {
        super(dsl);
        this.table = table;

        dataProvider = DataProvider.fromFilteringCallbacks(this::fetch, this::count).withConfigurableFilter();

        grid = new Grid<>();
        grid.setHeightFull();
        grid.setDataProvider(dataProvider);

        setHeightFull();
    }

    protected abstract Condition initialCondition();

    protected abstract Field<?>[] initialSort();

    @Override
    protected void refreshAll() {
        dataProvider.refreshAll();
    }

    Stream<R> fetch(Query<R, String> query) {
        return dsl
                .selectFrom(table)
                .where(createCondition(query))
                .orderBy(createOrderBy(query))
                .offset(query.getOffset())
                .limit(query.getLimit())
                .stream();
    }

    int count(Query<R, String> query) {
        return dsl
                .selectCount()
                .from(table)
                .where(createCondition(query))
                .fetchOneInto(Integer.class);
    }

    private Condition createCondition(Query<R, String> query) {
        Condition condition = noCondition();
        if (query.getFilter().isPresent()) {
            for (Field<?> field : table.fields()) {
                if (field.getType() == String.class) {
                    condition = condition.or(upper((Field<String>) field).like(upper("%" + query.getFilter().get() + "%")));
                } else {
                    condition = condition.or(field.like("%" + query.getFilter().get() + "%"));
                }
            }
        }
        condition = condition.and(initialCondition());
        return condition;
    }

    private Field<?>[] createOrderBy(Query<R, String> query) {
        if (query.getSortOrders().isEmpty()) {
            return initialSort();
        } else {
            for (QuerySortOrder sortOrder : query.getSortOrders()) {
                System.out.println("sortOrder :" + sortOrder);
            }
            return new Field[]{ATHLETE.GENDER, ATHLETE.YEAR_OF_BIRTH, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME};
        }
    }

}
