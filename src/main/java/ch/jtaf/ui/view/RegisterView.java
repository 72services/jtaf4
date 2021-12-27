package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.SecurityUserRecord;
import ch.jtaf.db.tables.records.UserGroupRecord;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static ch.jtaf.db.tables.SecurityGroup.SECURITY_GROUP;

@AnonymousAllowed
@Route(value = "register", layout = MainLayout.class)
public class RegisterView extends VerticalLayout implements HasDynamicTitle {

    public RegisterView(DSLContext dsl, TransactionTemplate transactionTemplate, PasswordEncoder passwordEncoder, JavaMailSender mailSender,
                        @Value("${jtaf.public.address}") String publicAddress) {
        addClassName("p-xl");

        var formLayout = new FormLayout();
        add(formLayout);

        var firstName = new TextField(getTranslation("First.Name"));
        firstName.setRequired(true);
        var lastName = new TextField(getTranslation("Last.Name"));
        lastName.setRequired(true);
        var email = new EmailField(getTranslation("Email"));
        var password = new PasswordField(getTranslation("Password"));
        password.setRequired(true);

        var register = new Button(getTranslation("Register"), e ->
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                var user = new SecurityUserRecord();
                user.setFirstName(firstName.getValue());
                user.setLastName(lastName.getValue());
                user.setEmail(email.getValue());
                user.setSecret(passwordEncoder.encode(password.getValue()));
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
                message.setSubject(getTranslation("Confirm.Email"));
                message.setText("""
                    Welcome to JTAF
                                        
                    %s/confirm?cf=%s
                    """.formatted(publicAddress, user.getConfirmationId())
                );
                mailSender.send(message);
            }));

        formLayout.add(firstName, lastName, email, password, register);

        firstName.focus();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Register");
    }
}
