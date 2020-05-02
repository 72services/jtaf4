package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.OrganizationRecord;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

public abstract class ProtectedView extends VerticalLayout implements BeforeEnterObserver {

    OrganizationRecord organizationRecord;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        organizationRecord = UI.getCurrent().getSession().getAttribute(OrganizationRecord.class);

        if (organizationRecord == null) {
            event.rerouteTo(OrganizationView.class);
        } else {
            loadData();
        }
    }

    abstract void loadData();

}
