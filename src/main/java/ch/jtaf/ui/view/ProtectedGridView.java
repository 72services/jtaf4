package ch.jtaf.ui.view;

import ch.jtaf.ui.component.JooqDataProviderProducer;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SortField;
import org.jooq.Table;

import java.io.Serial;

public abstract class ProtectedGridView<R extends Record> extends ProtectedView {

    @Serial
    private static final long serialVersionUID = 1L;

    final ConfigurableFilterDataProvider<R, Void, String> dataProvider;
    final Grid<R> grid;

    public ProtectedGridView(DSLContext dsl, Table<R> table) {
        super(dsl);

        dataProvider = new JooqDataProviderProducer<>(dsl, table, this::initialCondition, this::initialSort).getDataProvider();

        grid = new Grid<>();
        grid.setHeightFull();
        grid.setItems(dataProvider);

        setHeightFull();
    }

    protected abstract Condition initialCondition();

    protected abstract SortField<?>[] initialSort();

    @Override
    protected void refreshAll() {
        dataProvider.refreshAll();
    }

}
