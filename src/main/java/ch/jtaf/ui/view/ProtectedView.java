package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.OrganizationRecord;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import jakarta.annotation.security.RolesAllowed;
import org.jooq.DSLContext;

import java.io.Serial;

@RolesAllowed({Role.USER, Role.ADMIN})
public abstract class ProtectedView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    @Serial
    private static final long serialVersionUID = 1L;

    final transient DSLContext dsl;
    final transient OrganizationProvider organizationProvider;

    OrganizationRecord organizationRecord;

    ProtectedView(DSLContext dsl, OrganizationProvider organizationProvider) {
        this.dsl = dsl;
        this.organizationProvider = organizationProvider;

        setHeightFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        organizationRecord = organizationProvider.getOrganization();

        if (organizationRecord == null) {
            event.rerouteTo(OrganizationsView.class);
        } else {
            refreshAll();
        }
    }

    protected abstract void refreshAll();
}
