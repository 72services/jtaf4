package ch.jtaf.security;

import ch.jtaf.db.tables.records.SecurityUserRecord;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;

public class SecurityContext {

    public static final String USER = "user";
    public static final String ROLES = "roles";

    public static boolean isUserLoggedIn() {
        return VaadinSession.getCurrent() != null && VaadinSession.getCurrent().getAttribute(USER) != null;
    }

    public static SecurityUserRecord getUser() {
        return (SecurityUserRecord) VaadinSession.getCurrent().getAttribute(USER);
    }

    public static List<String> getRoles() {
        return (List<String>) VaadinSession.getCurrent().getAttribute(ROLES);
    }
}
