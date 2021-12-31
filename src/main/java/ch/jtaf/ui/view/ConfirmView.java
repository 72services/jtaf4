package ch.jtaf.ui.view;

import ch.jtaf.service.UserService;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@SuppressWarnings("unused")
@AnonymousAllowed
@Route(value = "confirm", layout = MainLayout.class)
public class ConfirmView extends VerticalLayout implements HasDynamicTitle, BeforeEnterObserver {

    private final transient UserService userService;
    private final VerticalLayout okDiv;
    private final H1 failure;

    public ConfirmView(UserService userService) {
        this.userService = userService;

        okDiv = new VerticalLayout();
        okDiv.add(new H1(getTranslation("Confirm.success")));
        okDiv.add(new RouterLink("Login", OrganizationsView.class));
        add(okDiv);

        failure = new H1(getTranslation("Confirm.failure"));
        add(failure);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        var queryParameters = beforeEnterEvent.getLocation().getQueryParameters();
        if (queryParameters.getParameters().containsKey("cf")) {
            boolean confirmed = userService.confirm(queryParameters.getParameters().get("cf").get(0));

            if (confirmed) {
                okDiv.setVisible(true);
                failure.setVisible(false);
            } else {
                okDiv.setVisible(false);
                failure.setVisible(true);
            }
        }
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Confirm");
    }
}
