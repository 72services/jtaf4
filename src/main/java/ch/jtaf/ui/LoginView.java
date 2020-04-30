package ch.jtaf.ui;

import ch.jtaf.service.LoginService;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

@Tag("sa-login-view")
@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout implements BeforeEnterObserver, HasUrlParameter<String> {

    private final LoginOverlay login = new LoginOverlay();
    private String redirect;
    private QueryParameters queryParameters;

    public LoginView(LoginService loginService) {
        login.setTitle("JTAF - Login");
        login.setDescription("Track and Field");
        login.setOpened(true);

        login.addLoginListener(event -> {
            boolean loggedIn = loginService.login(event.getUsername(), event.getPassword());
            if (loggedIn) {
                UI.getCurrent().navigate(redirect, queryParameters);
                login.close();
            } else {
                login.setError(true);
            }
        });

        getElement().appendChild(login.getElement());

        UI.getCurrent().getPage().executeJs("document.getElementById('vaadinLoginUsername').focus();");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        login.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        redirect = parameter;
        queryParameters = event.getLocation().getQueryParameters();
    }
}
