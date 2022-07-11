package ch.jtaf.service;

import ch.jtaf.db.tables.records.SecurityUserRecord;
import ch.jtaf.db.tables.records.UserGroupRecord;
import com.vaadin.flow.i18n.I18NProvider;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

import static ch.jtaf.db.tables.SecurityGroup.SECURITY_GROUP;
import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;

@SuppressWarnings("ClassCanBeRecord")
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final DSLContext dsl;
    private final PasswordEncoder passwordEncoder;
    private final I18NProvider i18n;
    private final String publicAddress;

    public UserService(DSLContext dsl, PasswordEncoder passwordEncoder, I18NProvider i18n,
                       @Value("${jtaf.public.address}") String publicAddress) {
        this.dsl = dsl;
        this.passwordEncoder = passwordEncoder;
        this.i18n = i18n;
        this.publicAddress = publicAddress;
    }

    @Transactional(rollbackFor = UserAlreadyExistException.class)
    public SecurityUserRecord createUser(String firstName, String lastName, String email, String password) throws UserAlreadyExistException {

        var count = dsl.selectCount().from(SECURITY_USER).where(SECURITY_USER.EMAIL.eq(email)).fetchOneInto(Integer.class);
        if (count != null && count > 0) {
            throw new UserAlreadyExistException();
        }
        var user = new SecurityUserRecord();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setSecret(passwordEncoder.encode(password));
        user.setConfirmationId(UUID.randomUUID().toString());
        user.setConfirmed(false);
        dsl.attach(user);
        user.store();

        var group = dsl.selectFrom(SECURITY_GROUP).where(SECURITY_GROUP.NAME.eq("USER")).fetchOne();

        if (group != null) {
            var userGroup = new UserGroupRecord(user.getId(), group.getId());
            dsl.attach(userGroup);
            userGroup.store();
        } else {
            throw new IllegalStateException("USER role does not exist!");
        }

        return user;
    }

    public void sendConfirmationEmail(SecurityUserRecord user, Locale locale) {
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            var message = new MimeMessage(session);
            message.setFrom(new InternetAddress("noreply@jtaf-cloud.appspotmail.com"));
            message.addRecipients(Message.RecipientType.TO, user.getEmail());
            message.setSubject(i18n.getTranslation("Confirm.Email.Subject", locale));
            message.setText(i18n.getTranslation("Confirm.Email.Body", locale, publicAddress, user.getConfirmationId()));

            Transport.send(message);
        } catch (MessagingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Transactional
    public boolean confirm(String confirmationId) {
        var securityUser = dsl
            .selectFrom(SECURITY_USER)
            .where(SECURITY_USER.CONFIRMATION_ID.eq(confirmationId))
            .fetchOne();

        if (securityUser != null) {
            securityUser.setConfirmed(true);
            securityUser.store();

            return true;
        } else {
            return false;
        }
    }
}
