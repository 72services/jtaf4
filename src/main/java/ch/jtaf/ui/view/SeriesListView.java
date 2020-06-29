package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.layout.MainLayout;
import ch.jtaf.util.LogoUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.Series.SERIES;

@Route(layout = MainLayout.class)
public class SeriesListView extends ProtectedGridView<SeriesRecord> {

    public SeriesListView(DSLContext dsl) {
        super(dsl, SERIES);

        setHeightFull();

        add(new H1(getTranslation("Series")));

        Button add = new Button(getTranslation("Add"));
        add.addClickListener(event -> {
            UI.getCurrent().navigate(SeriesView.class);
        });

        grid.addComponentColumn(LogoUtil::resizeLogo).setHeader(getTranslation("Logo"));
        grid.addColumn(SeriesRecord::getName).setHeader(getTranslation("Name")).setSortable(true);

        grid.addColumn(seriesRecord -> dsl
                .select(DSL.count(CATEGORY_ATHLETE.ATHLETE_ID))
                .from(CATEGORY_ATHLETE)
                .join(CATEGORY).on(CATEGORY.ID.eq(CATEGORY_ATHLETE.CATEGORY_ID))
                .where(CATEGORY.SERIES_ID.eq(seriesRecord.getId()))
                .fetchOneInto(Integer.class))
                .setHeader(getTranslation("Number.of.Athletes"));

        grid.addComponentColumn(seriesRecord -> {
            Checkbox hidden = new Checkbox();
            hidden.setReadOnly(true);
            hidden.setValue(seriesRecord.getHidden());
            return hidden;
        }).setHeader(getTranslation("Hidden"));
        grid.addComponentColumn(seriesRecord -> {
            Checkbox locked = new Checkbox();
            locked.setReadOnly(true);
            locked.setValue(seriesRecord.getLocked());
            return locked;
        }).setHeader(getTranslation("Hidden"));

        grid.addComponentColumn(seriesRecord -> {
            Button delete = new Button(getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event -> {
                try {
                    dsl.attach(seriesRecord);
                    seriesRecord.delete();
                } catch (DataAccessException e) {
                    Notification.show(e.getMessage());
                }
            });

            HorizontalLayout horizontalLayout = new HorizontalLayout(delete);
            horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(add);

        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(seriesRecord -> UI.getCurrent().navigate(SeriesView.class, "" + seriesRecord.getId())));

        add(grid);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Series");
    }

    @Override
    protected Condition initialCondition() {
        return SERIES.ORGANIZATION_ID.eq(organizationRecord.getId());
    }

    @Override
    protected Field<?>[] initialSort() {
        return new Field[]{SERIES.NAME};
    }
}
