package ch.jtaf.ui.view;

import ch.jtaf.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

public class OrganizationsViewTest extends KaribuTest {

    @WithMockUser(value = "simon@martinelli.ch", roles = "ADMIN")
    @Test
    void title_is_present() {
        UI.getCurrent().navigate(OrganizationsView.class);

        H1 h1 = _get(H1.class, spec -> spec.withId("view-title"));

        assertThat(h1.getText()).isEqualTo("Organizations");
    }
}
