package ch.jtaf.ui.view;

import ch.jtaf.ui.component.JooqDataProviderProducer;
import ch.jtaf.ui.security.OrganizationProvider;
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

    protected ProtectedGridView(DSLContext dsl, OrganizationProvider organizationProvider, Table<R> table) {
        super(dsl, organizationProvider);

        grid = new Grid<>();
        grid.setHeightFull();

        dataProvider = new JooqDataProviderProducer<>(dsl, grid, table, this::initialCondition, this::initialSort).getDataProvider();

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
