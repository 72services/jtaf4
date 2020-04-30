package ch.jtaf.security;

import org.jooq.DSLContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static ch.jtaf.db.tables.SecurityGroup.SECURITY_GROUP;
import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;
import static ch.jtaf.db.tables.UserGroup.USER_GROUP;

@Component
public class LoginUserDetailService implements UserDetailsService {

    private final DSLContext dsl;

    public LoginUserDetailService(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var securityUserRecord = dsl
                .selectFrom(SECURITY_USER)
                .where(SECURITY_USER.EMAIL.eq(username))
                .fetchOne();

        if (securityUserRecord != null) {
            var groups = dsl
                    .select(SECURITY_GROUP.NAME)
                    .from(USER_GROUP)
                    .join(SECURITY_GROUP).on(SECURITY_GROUP.ID.eq(USER_GROUP.GROUP_ID))
                    .fetch();

            return new User(securityUserRecord.getEmail(), securityUserRecord.getSecret(),
                    groups.stream()
                            .map(group -> new SimpleGrantedAuthority("ROLE_" + group))
                            .collect(Collectors.toList()));
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}
