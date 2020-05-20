package ch.jtaf.repository;

import com.vaadin.flow.data.provider.SortDirection;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectSeekStepN;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.vaadin.flow.data.provider.SortDirection.ASCENDING;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;

/**
 * Convenience methods using {@link DSLContext}
 */
public class JooqRepository {

    private final DSLContext dslContext;

    /**
     * Requires {@link DSLContext}
     *
     * @param dslContext The DSLContext to use
     */
    public JooqRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    /**
     * Convenience findAll
     *
     * @param table     The table to select from
     * @param condition A condition for the where clause
     * @param orderBy   A map fields for sorting
     * @param offset    The start offset
     * @param limit     The number of records to return
     * @param <T>       The Record type
     * @return List of Records
     */
    public <T extends Record> List<T> findAll(Table<T> table, Condition condition, Map<Field<?>, SortDirection> orderBy, int offset, int limit) {
        SelectConditionStep<T> where;
        if (condition == null) {
            where = dslContext.selectFrom(table)
                    .where(DSL.noCondition());
        } else {
            where = dslContext.selectFrom(table)
                    .where(condition);
        }
        if (orderBy != null && !orderBy.isEmpty()) {
            return createOrderBy(table, where, orderBy)
                    .offset(offset)
                    .limit(limit)
                    .fetch();
        } else {
            return where
                    .offset(offset)
                    .limit(limit)
                    .fetch();
        }
    }

    /**
     * Count method similar to @see findAll
     *
     * @param table     The table to count the records
     * @param condition A condition for the where clause
     * @param <T>       The Record type
     * @return Number of records
     */
    public <T extends Record> int count(Table<T> table, Condition condition) {
        if (condition == null) {
            return dslContext.fetchCount(dslContext.selectFrom(table));
        } else {
            return dslContext.fetchCount(dslContext.selectFrom(table).where(condition));
        }
    }

    /**
     * Base on map of Fields this method adds an order by
     *
     * @param table        The table
     * @param where        The where condition
     * @param orderColumns The map that contains the order fields
     * @param <T>          Record type
     * @return The select step with the order by clause
     */
    private <T extends Record> SelectSeekStepN<T> createOrderBy(Table<T> table, SelectConditionStep<T> where, Map<Field<?>, SortDirection> orderColumns) {
        List<OrderField<?>> orderFields = new ArrayList<>();
        orderColumns.forEach((key, value) -> {
            List<String> qualifiers = new ArrayList<>();
            qualifiers.add(table.getSchema().getName());
            qualifiers.addAll(Arrays.asList(table.getQualifiedName().getName()));
            qualifiers.add(key.getName());

            Name column = name(qualifiers);
            Field<Object> field = field(column);

            orderFields.add(value == ASCENDING ? field.asc() : field.desc());
        });
        return where.orderBy(orderFields);
    }

}
