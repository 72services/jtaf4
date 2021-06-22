package ch.jtaf.ui.security;

import ch.jtaf.db.tables.records.OrganizationRecord;
import com.vaadin.flow.component.UI;

public class OrganizationHolder {

    private OrganizationHolder() {
    }

    public static OrganizationRecord getOrganization() {
        return UI.getCurrent().getSession().getAttribute(OrganizationRecord.class);
    }

    public static void setOrganization(OrganizationRecord organization) {
        UI.getCurrent().getSession().setAttribute(OrganizationRecord.class, organization);
    }
}
