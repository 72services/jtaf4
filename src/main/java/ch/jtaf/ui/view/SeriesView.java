package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.dialog.SeriesDialog;
import ch.jtaf.ui.layout.MainLayout;
import ch.jtaf.util.LogoUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.Organization.ORGANIZATION;
import static ch.jtaf.db.tables.Series.SERIES;

@PageTitle("JTAF - Organizations")
@Route(layout = MainLayout.class)
public class SeriesView extends ProtectedView {

    private final DSLContext dsl;
    private final Grid<SeriesRecord> grid;

    public SeriesView(DSLContext dsl) {
        this.dsl = dsl;

        setHeightFull();

        add(new H1(getTranslation("Series")));

        SeriesDialog dialog = new SeriesDialog(getTranslation("Series"));

        Button add = new Button(getTranslation("Add.Series"));
        add.addClickListener(event -> dialog.open(SERIES.newRecord(), this::loadData));

        grid = new Grid<>();
        grid.setHeightFull();

        grid.addComponentColumn(LogoUtil::resizeLogo).setHeader(getTranslation("Logo"));
        grid.addColumn(SeriesRecord::getName).setHeader(getTranslation("Name"));

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
                .ifPresent(seriesRecord -> dialog.open(seriesRecord, this::loadData)));

        add(grid);
    }

    @Override
    void loadData() {
        var series = dsl
                .selectFrom(SERIES)
                .where(SERIES.ORGANIZATION_ID.eq(organizationRecord.getId()))
                .fetch();

        grid.setItems(series);
    }
}
