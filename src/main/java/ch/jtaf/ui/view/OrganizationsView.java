package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.configuration.security.Role;
import ch.jtaf.configuration.security.SecurityContext;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.db.tables.records.OrganizationUserRecord;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.dialog.OrganizationDialog;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serial;

import static ch.jtaf.db.tables.Organization.ORGANIZATION;
import static ch.jtaf.db.tables.OrganizationUser.ORGANIZATION_USER;
import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;

@RolesAllowed({Role.USER, Role.ADMIN})
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

        var dialog = new OrganizationDialog(getTranslation("Organization"));

        var add = new Button(getTranslation("Add"));
        add.setId("add-button");
        add.addClickListener(event -> {
            var organizationRecord = ORGANIZATION.newRecord();
            organizationRecord.setOwner(SecurityContext.getUsername());
            dialog.open(organizationRecord, this::loadData);
        });

        grid = new Grid<>();
        grid.setId("organizations-grid");
        grid.getClassNames().add("rounded-corners");
        grid.setHeightFull();

        grid.addColumn(OrganizationRecord::getOrganizationKey).setHeader(getTranslation("Key")).setSortable(true).setAutoWidth(true).setKey(ORGANIZATION.ORGANIZATION_KEY.getName());
        grid.addColumn(OrganizationRecord::getName).setHeader(getTranslation("Name")).setSortable(true).setAutoWidth(true).setKey(ORGANIZATION.NAME.getName());

        grid.addComponentColumn(organizationRecord -> {
            var select = new Button(getTranslation("Select"));
            select.setId("select-organization-" + organizationRecord.getOrganizationKey());
            select.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            select.addClickListener(event -> {
                organizationProvider.setOrganization(organizationRecord);
                UI.getCurrent().navigate(SeriesListView.class);
            });

            var delete = new Button(getTranslation("Delete"));
            delete.setId("delete-organization-" + organizationRecord.getOrganizationKey());
            delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event ->
                new ConfirmDialog(
                    "delete-organization-confirm-dialog",
                    getTranslation("Confirm"),
                    getTranslation("Are.you.sure"),
                    getTranslation("Delete"), e -> transactionTemplate.executeWithoutResult(transactionStatus -> {
                    try {
                        dsl.deleteFrom(ORGANIZATION_USER).where(ORGANIZATION_USER.ORGANIZATION_ID.eq(organizationRecord.getId())).execute();

                        dsl.attach(organizationRecord);
                        organizationRecord.delete();

                        loadData(null);
                    } catch (DataAccessException ex) {
                        Notification.show(ex.getMessage());
                    }
                }),
                    getTranslation("Cancel"), e -> {
                }).open());

            var horizontalLayout = new HorizontalLayout(select, delete);
            horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(add).setAutoWidth(true).setKey("edit-column");

        grid.addItemClickListener(event -> dialog.open(event.getItem(), this::loadData));

        loadData(null);

        add(grid);
    }

    private void loadData(OrganizationRecord organizationRecord) {
        if (organizationRecord != null) {
            transactionTemplate.executeWithoutResult(transactionStatus ->
                dsl.selectFrom(SECURITY_USER)
                    .where(SECURITY_USER.EMAIL.eq(SecurityContext.getUsername()))
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
            .where(ORGANIZATION_USER.securityUser().EMAIL.eq(SecurityContext.getUsername()))
            .fetch().into(ORGANIZATION);

        grid.setItems(organizations);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Organizations");
    }
}
