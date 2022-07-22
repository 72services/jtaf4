package ch.jtaf.ui.view;

import ch.jtaf.ui.PlaywrightIT;
import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DashboardViewIT extends PlaywrightIT {

    @Test
    void series_are_displayed() {
        page.navigate("http://localhost:8484/");

        Locator viewTitle = page.locator("#view-title");
        assertThat(viewTitle.innerText()).isEqualTo("Dashboard");

        Locator seriesLayouts = page.locator(".series-layout");
        assertThat(seriesLayouts.count()).isEqualTo(6);
    }
}
