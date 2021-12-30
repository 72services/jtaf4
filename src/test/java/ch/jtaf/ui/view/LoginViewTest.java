package ch.jtaf.ui.view;

import ch.jtaf.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginOverlay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.LoginFormKt._login;
import static org.assertj.core.api.Assertions.assertThat;

class LoginViewTest extends KaribuTest {

    @BeforeEach
    void login() {
        UI.getCurrent().navigate(LoginView.class);
    }

    @Test
    void login_with_unknown_user() {
        _login(_get(LoginOverlay.class), "not.existing@user.com", "pass");

        assertThat(_get(LoginOverlay.class).getElement().getOuterHTML()).isEqualTo("<vaadin-login-overlay></vaadin-login-overlay>");
    }

}
