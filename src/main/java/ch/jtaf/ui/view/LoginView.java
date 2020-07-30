package ch.jtaf.ui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import ch.jtaf.security.SecurityContext;

@Route
@PageTitle("JTAF - Login")
public class LoginView extends LoginOverlay implements AfterNavigationObserver, BeforeEnterObserver {
	
	private static final long serialVersionUID = 1L;

    public LoginView() {
        LoginI18n i18n = LoginI18n.createDefault();

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("JTAF");
        i18n.getHeader().setDescription("Track and Field");
        i18n.setAdditionalInformation(null);

        i18n.setForm(new LoginI18n.Form());
        i18n.getForm().setSubmit(getTranslation("Sign.in"));
        i18n.getForm().setTitle(getTranslation("Sign.in"));
        i18n.getForm().setUsername(getTranslation("Email"));
        i18n.getForm().setPassword(getTranslation("Password"));

        setI18n(i18n);

        setForgotPasswordButtonVisible(false);

        setAction("login");

        UI.getCurrent().getPage().executeJs("document.getElementById('vaadinLoginUsername').focus();");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityContext.isUserLoggedIn()) {
            event.forwardTo(DashboardView.class);
        } else {
            setOpened(true);
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

}
