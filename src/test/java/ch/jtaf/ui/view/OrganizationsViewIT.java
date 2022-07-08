package ch.jtaf.ui.view;

import ch.jtaf.ui.PlaywrightIT;
import ch.jtaf.ui.element.GridElement;
import ch.jtaf.ui.po.LoginPO;
import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganizationsViewIT extends PlaywrightIT {

    @BeforeEach
    void login() {
        page.navigate("http://localhost:8484/organizations");
        new LoginPO(page).login(System.getenv("JTAF4_TEST_USERNAME"), System.getenv("JTAF4_TEST_PASSWORD"));

        Locator locator = page.locator("#view-title");
        assertThat(locator.innerText()).isEqualTo("Organisationen");
    }

    @Test
    void add_organization() {
        GridElement organizationsGrid = new GridElement(page.locator("id=organizations-grid").elementHandle());

        organizationsGrid.waitForCellByTextContent("CIS");
        organizationsGrid.waitForCellByTextContent("Concours InterSection");
        organizationsGrid.waitForCellByTextContent("TVE");
        organizationsGrid.waitForCellByTextContent("Turnverein Erlach");

        page.locator("id=add-button").click();

        page.locator("id=vaadin-text-field-0").fill("TST");
        page.locator("id=vaadin-text-field-1").fill("Test");
        page.locator("id=edit-save").click();

        organizationsGrid.waitForCellByTextContent("TST");
        organizationsGrid.waitForCellByTextContent("Test");

        page.locator("id=delete-organization-TST").click();
        page.locator("id=delete-organization-confirm-dialog-confirm").click();
    }
}
