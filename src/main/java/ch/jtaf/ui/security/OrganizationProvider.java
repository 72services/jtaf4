package ch.jtaf.ui.security;

import ch.jtaf.db.tables.records.OrganizationRecord;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static ch.jtaf.db.tables.Organization.ORGANIZATION;

@VaadinSessionScope
@Component
public class OrganizationProvider {

    private static final String JTAF_ORGANIZATION_ID = "jtaf-organization-id";

    private final DSLContext dsl;

    private OrganizationRecord organization;

    public OrganizationProvider(DSLContext dsl) {
        this.dsl = dsl;
    }

    public OrganizationRecord getOrganization() {
        if (organization == null) {
            loadOrganizationFromCookie();
        }
        return organization;
    }

    public void setOrganization(OrganizationRecord organization) {
        this.organization = organization;
        saveOrganizationToCookie();
    }

    private void loadOrganizationFromCookie() {
        HttpServletRequest request = (HttpServletRequest) VaadinRequest.getCurrent();

        if (request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Arrays.stream(cookies)
                    .filter(cookie -> cookie.getName().equals(JTAF_ORGANIZATION_ID))
                    .findFirst()
                    .map(Cookie::getValue)
                    .ifPresent(s -> {
                        organization = dsl
                            .selectFrom(ORGANIZATION)
                            .where(ORGANIZATION.ID.eq(Long.parseLong(s)))
                            .fetchOne();
                    });
            }
        }
    }

    private void saveOrganizationToCookie() {
        HttpServletResponse response = (HttpServletResponse) VaadinResponse.getCurrent();

        Cookie cookie = new Cookie(JTAF_ORGANIZATION_ID, organization.getId().toString());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);
    }
}
