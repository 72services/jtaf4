package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.security.OrganizationHolder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import org.jooq.DSLContext;

public abstract class ProtectedView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    private static final long serialVersionUID = 1L;

    final transient DSLContext dsl;

    OrganizationRecord organizationRecord;

    ProtectedView(DSLContext dsl) {
        this.dsl = dsl;

        setHeightFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        organizationRecord = OrganizationHolder.getOrganization();

        if (organizationRecord == null) {
            event.rerouteTo(OrganizationsView.class);
        } else {
            refreshAll();
        }
    }

    protected abstract void refreshAll();
}
