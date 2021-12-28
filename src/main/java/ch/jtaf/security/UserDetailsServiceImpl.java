package ch.jtaf.security;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static ch.jtaf.db.tables.SecurityGroup.SECURITY_GROUP;
import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;
import static ch.jtaf.db.tables.UserGroup.USER_GROUP;

@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    private final DSLContext dsl;

    public UserDetailsServiceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var securityUserRecord = dsl
            .selectFrom(SECURITY_USER)
            .where(SECURITY_USER.EMAIL.eq(username))
            .fetchOne();

        if (securityUserRecord != null) {
            var groups = dsl
                .select(USER_GROUP.securityGroup().NAME)
                .from(USER_GROUP)
                .fetch();

            return new User(securityUserRecord.getEmail(), securityUserRecord.getSecret(),
                groups.stream()
                    .map(group -> new SimpleGrantedAuthority("ROLE_" + group.getValue(SECURITY_GROUP.NAME)))
                    .toList());
        } else {
            throw new UsernameNotFoundException(username);
        }
    }
}
