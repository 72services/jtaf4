package ch.jtaf.ui.view;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SortField;
import org.jooq.Table;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;

import ch.jtaf.ui.component.JooqDataProviderProducer;

public abstract class ProtectedGridView<R extends Record> extends ProtectedView {
	
	private static final long serialVersionUID = 1L;

	final Table<R> table;
	final ConfigurableFilterDataProvider<R, Void, String> dataProvider;
	final Grid<R> grid;

	public ProtectedGridView(DSLContext dsl, Table<R> table) {
		super(dsl);
		this.table = table;

		dataProvider = new JooqDataProviderProducer<>(dsl, table, this::initialCondition, this::initialSort).getDataProvider();

		grid = new Grid<>();
		grid.setHeightFull();
		grid.setDataProvider(dataProvider);

		setHeightFull();
	}

	protected abstract Condition initialCondition();

	protected abstract SortField<?>[] initialSort();

	@Override
	protected void refreshAll() {
		dataProvider.refreshAll();
	}

}
