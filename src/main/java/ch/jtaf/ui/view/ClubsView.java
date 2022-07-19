package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.ui.dialog.ClubDialog;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.router.Route;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;

import java.io.Serial;

import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

@Route(layout = MainLayout.class)
public class ClubsView extends ProtectedGridView<ClubRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    public ClubsView(DSLContext dsl, OrganizationProvider organizationProvider) {
        super(dsl, organizationProvider, CLUB);

        setHeightFull();

        var dialog = new ClubDialog(getTranslation("Clubs"));

        grid.setId("clubs-grid");

        grid.addColumn(ClubRecord::getAbbreviation).setHeader(getTranslation("Abbreviation")).setSortable(true).setAutoWidth(true).setKey(CLUB.ABBREVIATION.getName());
        grid.addColumn(ClubRecord::getName).setHeader(getTranslation("Name")).setSortable(true).setAutoWidth(true).setKey(CLUB.NAME.getName());

        addActionColumnAndSetSelectionListener(grid, dialog, clubRecord -> refreshAll(),
            () -> {
                ClubRecord newRecord = CLUB.newRecord();
                newRecord.setOrganizationId(organizationRecord.getId());
                return newRecord;
            }, this::refreshAll);

        add(grid);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Clubs");
    }

    @Override
    protected Condition initialCondition() {
        return CLUB.ORGANIZATION_ID.eq(organizationRecord.getId());
    }

    @Override
    protected SortField<?>[] initialSort() {
        return new SortField[]{CLUB.ABBREVIATION.asc()};
    }
}
