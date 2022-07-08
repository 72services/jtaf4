package ch.jtaf.ui.view;

import ch.jtaf.ui.PlaywrightIT;
import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DashboardViewIT extends PlaywrightIT {

    @Test
    void title_is_dashboard() {
        Locator locator = page.locator("#view-title");

        assertThat(locator.innerText()).isEqualTo("Dashboard");
    }

    @Test
    void series_are_displayed() {
        Locator locator = page.locator(".series-layout");

        assertThat(locator.count()).isEqualTo(6);
    }
}
