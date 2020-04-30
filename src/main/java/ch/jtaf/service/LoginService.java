package ch.jtaf.service;

import com.vaadin.flow.server.VaadinSession;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import static ch.jtaf.db.tables.SecurityGroup.SECURITY_GROUP;
import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;
import static ch.jtaf.db.tables.UserGroup.USER_GROUP;

@Component
public class LoginService {

    private final DSLContext dsl;

    public LoginService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public boolean login(String username, String password) {
        var user = dsl
                .selectFrom(SECURITY_USER)
                .where(SECURITY_USER.EMAIL.eq(username))
                .and(SECURITY_USER.SECRET.eq(password))
                .fetchOne();

        if (user != null) {
            var groups = dsl
                    .select(SECURITY_GROUP.NAME)
                    .from(USER_GROUP)
                    .join(SECURITY_GROUP).on(SECURITY_GROUP.ID.eq(USER_GROUP.GROUP_ID))
                    .fetch();

            VaadinSession.getCurrent().setAttribute("user", user);
            VaadinSession.getCurrent().setAttribute("roles", groups.map(stringRecord1 -> stringRecord1.get(SECURITY_GROUP.NAME)));

            return true;
        }
        return false;
    }

}
