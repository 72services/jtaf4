package ch.jtaf.ui.view;

import ch.jtaf.ui.PlaywrightIT;
import ch.jtaf.ui.po.LoginPO;
import com.microsoft.playwright.Locator;
import in.virit.mopo.GridPw;
import in.virit.mopo.Mopo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrganizationsViewIT extends PlaywrightIT {

    @LocalServerPort
    private int port;

    @MockBean
    private JavaMailSender javaMailSender;

    private Mopo mopo;

    @BeforeEach
    void login() {
        page.navigate("http://localhost:" + port + "/organizations");

        mopo = new Mopo(page);

        new LoginPO(page).login("simon@martinelli.ch", "pass");

        Locator locator = page.locator("#view-title");
        assertThat(locator.innerText()).isEqualTo("Organizations");
    }

    @Test
    void add_organization() {
        var organizationsGrid = new GridPw(page.locator("id=organizations-grid"));

        var rowCis = organizationsGrid.getRow(0);
        assertThat(rowCis.getCell(0).innerText()).isEqualTo("CIS");
        var rowTve = organizationsGrid.getRow(1);
        assertThat(rowTve.getCell(0).innerText()).isEqualTo("TVE");

        page.locator("id=add-button").click();

        page.locator("vaadin-text-field[id='key'] > input").fill("TST");
        page.locator("vaadin-text-field[id='name'] > input").fill("Test");
        page.locator("id=edit-save").click();

        mopo.waitForConnectionToSettle();

        var rowTst = organizationsGrid.getRow(2);

        assertThat(rowTst.getCell(0).innerText()).isEqualTo("TST");

        page.locator("id=delete-organization-TST").click();
        page.locator("id=delete-organization-confirm-dialog-confirm").click();

        mopo.waitForConnectionToSettle();

        assertThat(organizationsGrid.getRenderedRowCount()).isEqualTo(2);
    }
}
