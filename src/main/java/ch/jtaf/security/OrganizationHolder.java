package ch.jtaf.security;

import com.vaadin.flow.component.UI;

import ch.jtaf.db.tables.records.OrganizationRecord;

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
