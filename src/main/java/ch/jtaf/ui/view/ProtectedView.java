package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.security.OrganizationHolder;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

public abstract class ProtectedView extends VerticalLayout implements BeforeEnterObserver {

    OrganizationRecord organizationRecord;

    public ProtectedView() {
        setHeightFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        organizationRecord = OrganizationHolder.getOrganization();

        if (organizationRecord == null) {
            event.rerouteTo(OrganizationsView.class);
        } else {
            loadData();
        }
    }

    abstract void loadData();

}
