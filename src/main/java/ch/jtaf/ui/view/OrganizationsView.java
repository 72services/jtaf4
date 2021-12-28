package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.db.tables.records.OrganizationUserRecord;
import ch.jtaf.ui.dialog.OrganizationDialog;
import ch.jtaf.ui.layout.MainLayout;
import ch.jtaf.ui.security.OrganizationProvider;
import ch.jtaf.ui.security.SecurityContext;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.security.RolesAllowed;
import java.io.Serial;

import static ch.jtaf.db.tables.Organization.ORGANIZATION;
import static ch.jtaf.db.tables.OrganizationUser.ORGANIZATION_USER;
import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;

@RolesAllowed({"USER", "ADMIN"})
@Route(layout = MainLayout.class)
public class OrganizationsView extends VerticalLayout implements HasDynamicTitle {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient DSLContext dsl;
    private final TransactionTemplate transactionTemplate;
    private final Grid<OrganizationRecord> grid;

    public OrganizationsView(DSLContext dsl, TransactionTemplate transactionTemplate, OrganizationProvider organizationProvider) {
        this.dsl = dsl;
        this.transactionTemplate = transactionTemplate;

        setHeightFull();

        OrganizationDialog dialog = new OrganizationDialog(getTranslation("Organization"));

        Button add = new Button(getTranslation("Add"));
        add.addClickListener(event -> {
            OrganizationRecord organizationRecord = ORGANIZATION.newRecord();
            organizationRecord.setOwner(SecurityContext.getUserEmail());
            dialog.open(organizationRecord, this::loadData);
        });

        grid = new Grid<>();
        grid.setId("organizations-grid");

        grid.getClassNames().add("rounded-corners");
        grid.setHeightFull();

        grid.addColumn(OrganizationRecord::getOrganizationKey).setHeader(getTranslation("Key")).setSortable(true);
        grid.addColumn(OrganizationRecord::getName).setHeader(getTranslation("Name")).setSortable(true);

        grid.addComponentColumn(organizationRecord -> {
            Button select = new Button(getTranslation("Select"));
            select.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            select.addClickListener(event -> {
                organizationProvider.setOrganization(organizationRecord);
                UI.getCurrent().navigate(SeriesListView.class);
            });

            Button delete = new Button(getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event -> {
                ConfirmDialog confirmDialog = new ConfirmDialog(getTranslation("Confirm"),
                    getTranslation("Are.you.sure"),
                    getTranslation("Delete"), e -> {
                    try {
                        dsl.attach(organizationRecord);
                        organizationRecord.delete();
                    } catch (DataAccessException ex) {
                        Notification.show(ex.getMessage());
                    }
                },
                    getTranslation("Cancel"), e -> {
                });
                confirmDialog.setConfirmButtonTheme("error primary");
                confirmDialog.open();
            });

            HorizontalLayout horizontalLayout = new HorizontalLayout(select, delete);
            horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(add).setKey("Edit");

        grid.addSelectionListener(event -> event.getFirstSelectedItem()
            .ifPresent(organization -> dialog.open(organization, this::loadData)));

        loadData(null);

        add(grid);
    }

    private void loadData(OrganizationRecord organizationRecord) {
        if (organizationRecord != null) {
            transactionTemplate.executeWithoutResult(transactionStatus ->
                dsl.selectFrom(SECURITY_USER)
                    .where(SECURITY_USER.EMAIL.eq(SecurityContext.getUserEmail()))
                    .fetchOptional()
                    .ifPresent(user -> {
                        var organizationUser = new OrganizationUserRecord();
                        organizationUser.setOrganizationId(organizationRecord.getId());
                        organizationUser.setUserId(user.getId());
                        dsl.attach(organizationUser);
                        organizationUser.store();
                    }));
        }

        var organizations = dsl
            .select(ORGANIZATION_USER.organization().fields())
            .from(ORGANIZATION_USER)
            .where(ORGANIZATION_USER.securityUser().EMAIL.eq(SecurityContext.getUserEmail()))
            .fetch().into(ORGANIZATION);

        grid.setItems(organizations);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Organizations");
    }
}
