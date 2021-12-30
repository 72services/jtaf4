package ch.jtaf.ui.view;

import ch.jtaf.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class ResultCapturingViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of("ADMIN"));
        UI.getCurrent().getPage().reload();
    }

    @Test
    void enter_results() {
        Button enterResults = _get(Button.class, spec -> spec.withId("enter-results-1"));
        enterResults.click();

        TextField filter = _get(TextField.class, spec -> spec.withId("filter"));
        filter.setValue("Martinelli");

        TextField result = _get(TextField.class, spec -> spec.withCaption("80 m"));
        assertThat(result.getValue()).isEqualTo("12.12");

        TextField points = _get(TextField.class, spec -> spec.withId("points-0"));
        assertThat(points.getValue()).isEqualTo("402");
    }
}
