package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.service.SeriesService;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.layout.MainLayout;
import ch.jtaf.ui.util.LogoUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.io.Serial;

import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.Series.SERIES;

@Route(layout = MainLayout.class)
public class SeriesListView extends ProtectedGridView<SeriesRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    public SeriesListView(DSLContext dsl, OrganizationProvider organizationProvider, SeriesService seriesService) {
        super(dsl, organizationProvider, SERIES);

        setHeightFull();

        var add = new Button(getTranslation("Add"));
        add.setId("add-series");
        add.addClickListener(event -> UI.getCurrent().navigate(SeriesView.class));

        grid.setId("series-grid");

        grid.addComponentColumn(LogoUtil::resizeLogo).setHeader(getTranslation("Logo")).setAutoWidth(true);
        grid.addColumn(SeriesRecord::getName).setHeader(getTranslation("Name")).setSortable(true).setAutoWidth(true).setKey(SERIES.NAME.getName());

        grid.addColumn(seriesRecord ->
                dsl
                    .select(DSL.count(CATEGORY_ATHLETE.ATHLETE_ID)).from(CATEGORY_ATHLETE)
                    .where(CATEGORY_ATHLETE.category().SERIES_ID.eq(seriesRecord.getId()))
                    .fetchOneInto(Integer.class)
            )
            .setHeader(getTranslation("Number.of.Athletes")).setAutoWidth(true);

        grid.addComponentColumn(seriesRecord -> {
            var hidden = new Checkbox();
            hidden.setReadOnly(true);
            hidden.setValue(seriesRecord.getHidden());
            return hidden;
        }).setHeader(getTranslation("Hidden")).setAutoWidth(true);
        grid.addComponentColumn(seriesRecord -> {
            var locked = new Checkbox();
            locked.setReadOnly(true);
            locked.setValue(seriesRecord.getLocked());
            return locked;
        }).setHeader(getTranslation("Locked")).setAutoWidth(true);

        grid.addComponentColumn(seriesRecord -> {
            var delete = new Button(getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event ->
                new ConfirmDialog(
                    "delete-series-confirm-dialog",
                    getTranslation("Confirm"),
                    getTranslation("Are.you.sure"),
                    getTranslation("Delete"), e -> {
                    try {
                        seriesService.deleteSeries(seriesRecord.getId());
                        refreshAll();
                    } catch (DataAccessException ex) {
                        Notification.show(ex.getMessage());
                    }
                },
                    getTranslation("Cancel"), e -> {
                }).open());

            var horizontalLayout = new HorizontalLayout(delete);
            horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(add).setAutoWidth(true).setKey("delete-column");

        grid.addItemClickListener(event -> UI.getCurrent().navigate(SeriesView.class, event.getItem().getId()));

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
    protected SortField<?>[] initialSort() {
        return new SortField[]{SERIES.NAME.desc()};
    }
}
