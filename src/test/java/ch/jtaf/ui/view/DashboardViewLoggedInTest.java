package ch.jtaf.ui.view;

import ch.jtaf.ui.AbstractAppTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Locale;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DashboardViewLoggedInTest extends AbstractAppTest {

    @BeforeEach
    void login() {
        login("simon@martinelli.ch", "*****", List.of("ADMIN"));
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
