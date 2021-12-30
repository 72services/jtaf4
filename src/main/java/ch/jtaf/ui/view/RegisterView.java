package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.SecurityUserRecord;
import ch.jtaf.service.UserAlreadyExistException;
import ch.jtaf.service.UserService;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@Route(value = "register", layout = MainLayout.class)
public class RegisterView extends VerticalLayout implements HasDynamicTitle {

    public RegisterView(UserService userService) {
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

        var register = new Button(getTranslation("Register"), e -> {
            try {
                SecurityUserRecord user = userService.createUser(firstName.getValue(), lastName.getValue(), email.getValue(), password.getValue());
                userService.sendConfirmationEmail(user, getLocale());

                Notification.show(getTranslation("Email.sent"), 5000, Notification.Position.TOP_END);
                UI.getCurrent().navigate(DashboardView.class);
            } catch (UserAlreadyExistException ex) {
                Notification.show(getTranslation("User.already.exist"), 5000, Notification.Position.TOP_END);
            }
        });

        formLayout.add(firstName, lastName, email, password, register);

        firstName.focus();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Register");
    }
}
