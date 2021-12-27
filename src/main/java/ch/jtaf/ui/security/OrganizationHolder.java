package ch.jtaf.ui.security;

import ch.jtaf.db.tables.records.OrganizationRecord;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.stereotype.Component;

@VaadinSessionScope
@Component
public class OrganizationHolder {

    private OrganizationRecord organization;

    public OrganizationRecord getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationRecord organization) {
        this.organization = organization;
    }
}
