package ch.jtaf.ui.view;

import ch.jtaf.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.Test;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class RegisterViewTest extends KaribuTest {

    @Test
    void register() {
        UI.getCurrent().navigate(RegisterView.class);

        _get(TextField.class, spec -> spec.withCaption("First Name")).setValue("John");
        _get(TextField.class, spec -> spec.withCaption("Last Name")).setValue("Doe");
        _get(EmailField.class, spec -> spec.withCaption("Email")).setValue("john@doe.dev");
        _get(PasswordField.class, spec -> spec.withCaption("Password")).setValue("pass");
        _get(Button.class, spec -> spec.withCaption("Register")).click();

        Notification notification = _get(Notification.class);
        assertThat(notification.getElement().getOuterHTML()).isEqualTo("""
            <vaadin-notification suppress-template-warning>
             <template>
              Thanks for registering. An email was sent to your address. Please check your inbox.
             </template>
            </vaadin-notification>""");
    }
}
