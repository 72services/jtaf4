package ch.jtaf.ui.component;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SortField;
import org.jooq.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.jooq.impl.DSL.noCondition;
import static org.jooq.impl.DSL.upper;

public class JooqDataProviderProducer<R extends Record> {

    private final DSLContext dsl;
    private final Table<R> table;
    private final ConfigurableFilterDataProvider<R, Void, String> dataProvider;
    private final Supplier<Condition> initialCondition;
    private final Supplier<SortField<?>[]> initialSort;

    public JooqDataProviderProducer(DSLContext dsl, Table<R> table, Supplier<Condition> initialCondition,
                                    Supplier<SortField<?>[]> initialSort) {
        this.dsl = dsl;
        this.table = table;
        this.initialCondition = initialCondition;
        this.initialSort = initialSort;

        this.dataProvider = DataProvider.fromFilteringCallbacks(this::fetch, this::count).withConfigurableFilter();
    }

    public ConfigurableFilterDataProvider<R, Void, String> getDataProvider() {
        return dataProvider;
    }

    private Stream<R> fetch(Query<R, String> query) {
        return dsl.selectFrom(table).where(createCondition(query)).orderBy(createOrderBy(query))
            .offset(query.getOffset()).limit(query.getLimit()).stream();
    }

    private int count(Query<R, String> query) {
        return dsl.selectCount().from(table).where(createCondition(query)).fetchOneInto(Integer.class);
    }

    private Condition createCondition(Query<R, String> query) {
        Condition condition = noCondition();
        Optional<String> filter = query.getFilter();
        if (filter.isPresent()) {
            for (Field<?> field : table.fields()) {
                if (field.getType() == String.class) {
                    //noinspection unchecked
                    condition = condition
                        .or(upper((Field<String>) field).like(upper("%" + filter.get() + "%")));
                } else {
                    condition = condition.or(field.like("%" + filter.get() + "%"));
                }
            }
        }
        condition = condition.and(initialCondition.get());
        return condition;
    }

    private SortField<?>[] createOrderBy(Query<R, String> query) {
        if (query.getSortOrders().isEmpty()) {
            return initialSort.get();
        } else {
            List<SortField<?>> sortFields = new ArrayList<>();
            for (QuerySortOrder sortOrder : query.getSortOrders()) {
                String column = sortOrder.getSorted();
                SortDirection sortDirection = sortOrder.getDirection();
                Field<?> field = table.field(column);
                if (sortDirection == SortDirection.DESCENDING) {
                    sortFields.add(field.desc());
                } else {
                    sortFields.add(field.asc());
                }
            }
            return sortFields.toArray(new SortField<?>[0]);
        }
    }
}
