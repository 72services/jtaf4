package ch.jtaf.ui.po;

import com.microsoft.playwright.Page;

public class LoginPO {

    private final Page page;

    public LoginPO(Page page) {
        this.page = page;
    }

    public void login(String username, String password) {
        page.fill("vaadin-text-field[name='username'] > input", username);
        page.fill("vaadin-password-field[name='password'] > input", password);
        page.click("vaadin-button");
    }
}
