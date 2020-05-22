package ch.jtaf.ui.component;

import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static org.jooq.impl.DSL.noCondition;
import static org.jooq.impl.DSL.upper;

public class JooqDataProviderProducer<R extends Record> {

    private final DSLContext dsl;
    private final Table<R> table;
    private final ConfigurableFilterDataProvider<R, Void, String> dataProvider;
    private final Supplier<Condition> initialCondition;
    private final Supplier<Field<?>[]> initialSort;

    public JooqDataProviderProducer(DSLContext dsl, Table<R> table, Supplier<Condition> initialCondition, Supplier<Field<?>[]> initialSort) {
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
        return dsl
                .selectFrom(table)
                .where(createCondition(query))
                .orderBy(createOrderBy(query))
                .offset(query.getOffset())
                .limit(query.getLimit())
                .stream();
    }

    private int count(Query<R, String> query) {
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
        condition = condition.and(initialCondition.get());
        return condition;
    }

    private Field<?>[] createOrderBy(Query<R, String> query) {
        if (query.getSortOrders().isEmpty()) {
            return initialSort.get();
        } else {
            for (QuerySortOrder sortOrder : query.getSortOrders()) {
                System.out.println("sortOrder :" + sortOrder);
            }
            return new Field[]{ATHLETE.GENDER, ATHLETE.YEAR_OF_BIRTH, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME};
        }
    }
}
