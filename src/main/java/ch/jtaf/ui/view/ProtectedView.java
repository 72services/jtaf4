package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.security.OrganizationHolder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import org.jooq.DSLContext;

import java.io.Serial;

public abstract class ProtectedView extends VerticalLayout implements BeforeEnterObserver, HasDynamicTitle {

    @Serial
    private static final long serialVersionUID = 1L;

    final transient DSLContext dsl;
    final transient OrganizationHolder organizationHolder;

    OrganizationRecord organizationRecord;

    ProtectedView(DSLContext dsl, OrganizationHolder organizationHolder) {
        this.dsl = dsl;
        this.organizationHolder = organizationHolder;

        setHeightFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        organizationRecord = organizationHolder.getOrganization();

        if (organizationRecord == null) {
            event.rerouteTo(OrganizationsView.class);
        } else {
            refreshAll();
        }
    }

    protected abstract void refreshAll();
}
