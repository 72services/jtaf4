package ch.jtaf.ui;

import ch.jtaf.security.SecurityUtils;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import javax.activation.DataHandler;

@Route
@PageTitle("JTAF - Track and Field")
public class LoginView extends LoginOverlay implements AfterNavigationObserver, BeforeEnterObserver {

    public LoginView() {
        LoginI18n i18n = LoginI18n.createDefault();

        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("JTAF - Track and Field");
        i18n.setAdditionalInformation(null);

        i18n.setForm(new LoginI18n.Form());
        i18n.getForm().setSubmit("Sign in");
        i18n.getForm().setTitle("Sign in");
        i18n.getForm().setUsername("Email");
        i18n.getForm().setPassword("Password");

        setI18n(i18n);

        setForgotPasswordButtonVisible(false);

        setAction("login");

        UI.getCurrent().getPage().executeJs("document.getElementById('vaadinLoginUsername').focus();");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityUtils.isUserLoggedIn()) {
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
