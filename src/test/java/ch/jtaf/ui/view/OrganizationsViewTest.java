package ch.jtaf.ui.view;

import ch.jtaf.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class OrganizationsViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));
    }

    @Test
    void navigation_to_organizations() {
        UI.getCurrent().navigate(OrganizationsView.class);

        H1 h1 = _get(H1.class, spec -> spec.withId("view-title"));

        assertThat(h1.getText()).isEqualTo("Organizations");
    }
}
