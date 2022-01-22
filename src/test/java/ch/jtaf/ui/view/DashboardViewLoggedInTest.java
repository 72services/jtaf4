package ch.jtaf.ui.view;

import ch.jtaf.ui.KaribuTest;
import ch.jtaf.ui.security.Role;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

class DashboardViewLoggedInTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of(Role.ADMIN));
    }

    @Test
    void enter_results_is_enabled() {
        UI.getCurrent().getPage().reload();

        _assert(Button.class, 6, spec -> spec.withCaption("Enter Results"));

        _get(Button.class, spec -> spec.withId("logout")).click();
    }

    @Test
    void resultate_eingeben_is_displayed() {
        Locale.setDefault(Locale.GERMAN);

        UI.getCurrent().getPage().reload();

        _assert(Button.class, 6, spec -> spec.withCaption("Resultate eingeben"));

        Locale.setDefault(Locale.ENGLISH);
    }
}
