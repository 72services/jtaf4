package ch.jtaf.ui;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class PlaywrightIT {

    private static Playwright playwright;
    private static Browser browser;
    protected Page page;

    @BeforeAll
    static void setUpClass() {
        playwright = Playwright.create();
        BrowserType browserType = playwright.chromium();
        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
        launchOptions.headless = false;
        browser = browserType.launch(launchOptions);
    }

    @AfterAll
    static void tearDownClass() {
        playwright.close();
    }

    @BeforeEach
    void setUp() {
        Browser.NewContextOptions newContextOptions = new Browser.NewContextOptions();
        newContextOptions.setViewportSize(1920, 1080);
        BrowserContext context = browser.newContext(newContextOptions);
        page = context.newPage();
        page.navigate("http://localhost:8484/");
    }

    @AfterEach
    void tearDown() {
        page.context().close();
    }
}
