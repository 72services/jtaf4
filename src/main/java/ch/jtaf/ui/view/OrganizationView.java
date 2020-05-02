package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.dialog.OrganizationDialog;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionTemplate;

import static ch.jtaf.db.tables.Organization.ORGANIZATION;
import static ch.jtaf.db.tables.OrganizationUser.ORGANIZATION_USER;
import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;

@PageTitle("JTAF - Organizations")
@Route(layout = MainLayout.class)
public class OrganizationView extends VerticalLayout {

    private final DSLContext dsl;
    private final Grid<OrganizationRecord> grid;

    public OrganizationView(DSLContext dsl, TransactionTemplate transactionTemplate) {
        this.dsl = dsl;

        add(new H1(getTranslation("My.Organizations")));

        Button add = new Button(getTranslation("Add.Organization"));

        grid = new Grid<>();
        grid.addColumn(OrganizationRecord::getOrganizationKey).setHeader(getTranslation("Key"));
        grid.addColumn(OrganizationRecord::getName).setHeader(getTranslation("Name"));

        grid.addComponentColumn(organizationRecord -> {
            Button select = new Button(getTranslation("Select"));
            select.addClickListener(event -> {
                UI.getCurrent().getSession().setAttribute(OrganizationRecord.class, organizationRecord);
                UI.getCurrent().getPage().reload();
            });

            Button delete = new Button(getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

            HorizontalLayout horizontalLayout = new HorizontalLayout(select, delete);
            horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(add);

        OrganizationDialog dialog = new OrganizationDialog();

        grid.addSelectionListener(event -> event.getFirstSelectedItem()
                .ifPresent(organization -> dialog.open(organization, this::loadData)));

        loadData();

        add(grid);
    }

    private void loadData() {
        var organizations = dsl
                .select()
                .from(ORGANIZATION)
                .join(ORGANIZATION_USER).on(ORGANIZATION_USER.ORGANIZATION_ID.eq(ORGANIZATION.ID))
                .join(SECURITY_USER).on(SECURITY_USER.ID.eq(ORGANIZATION_USER.USER_ID))
                .where(SECURITY_USER.EMAIL.eq(SecurityContextHolder.getContext().getAuthentication().getName()))
                .fetch().into(ORGANIZATION);

        grid.setItems(organizations);
    }

}
