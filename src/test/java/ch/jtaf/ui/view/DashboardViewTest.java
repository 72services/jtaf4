package ch.jtaf.ui.view;

import ch.jtaf.ui.AbstractAppTest;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.junit.jupiter.api.Test;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DashboardViewTest extends AbstractAppTest {

    @Test
    void title_is_present() {
        H1 h1 = _get(H1.class, spec -> spec.withId("view-title"));

        assertEquals("Dashboard", h1.getText());
    }

    @Test
    void series_are_displayed() {
        _assert(HorizontalLayout.class, 4, spec -> spec.withClasses("series-layout"));
    }
}
