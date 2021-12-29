package ch.jtaf.service;

import ch.jtaf.db.tables.records.SecurityUserRecord;
import ch.jtaf.db.tables.records.UserGroupRecord;
import com.vaadin.flow.i18n.I18NProvider;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

import static ch.jtaf.db.tables.SecurityGroup.SECURITY_GROUP;

@Service
public class UserService {

    private final DSLContext dsl;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final I18NProvider i18n;
    private final String publicAddress;

    public UserService(DSLContext dsl, PasswordEncoder passwordEncoder, JavaMailSender mailSender, I18NProvider i18n,
                       @Value("${jtaf.public.address}") String publicAddress) {
        this.dsl = dsl;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.i18n = i18n;
        this.publicAddress = publicAddress;
    }

    @Transactional
    public void createUserAndSendConfirmationEmail(String firstName, String lastName, String email, String password, Locale locale) {
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
            throw new IllegalStateException("No group with name USER found!");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@jtaf.ch");
        message.setTo(user.getEmail());
        message.setSubject(i18n.getTranslation("Confirm.Email.Subject", locale));
        message.setText(i18n.getTranslation("Confirm.Email.Body", locale, publicAddress, user.getConfirmationId()));
        mailSender.send(message);
    }
}
