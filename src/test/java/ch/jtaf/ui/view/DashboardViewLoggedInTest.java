package ch.jtaf.ui.view;

import ch.jtaf.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;

class DashboardViewLoggedInTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));
    }

    @Test
    void enter_results_is_enabled() {
        UI.getCurrent().getPage().reload();

        _assert(Button.class, 6, spec -> spec.withCaption("Enter Results"));
    }

    @Test
    void check_i18n() {
        Locale.setDefault(Locale.GERMAN);

        UI.getCurrent().getPage().reload();
        _assert(Button.class, 6, spec -> spec.withCaption("Resultate eingeben"));

        Locale.setDefault(Locale.ENGLISH);
    }
}
