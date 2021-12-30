package ch.jtaf.ui.view;

import ch.jtaf.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import org.junit.jupiter.api.Test;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;

class ConfirmationViewTest extends KaribuTest {

    @Test
    void confirm() {
        UI.getCurrent().navigate(ConfirmView.class);

        _assert(H1.class, 1, spec -> spec.withText("The confirmation was successful you can now login."));
    }
}
