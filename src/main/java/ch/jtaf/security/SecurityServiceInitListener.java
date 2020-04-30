package ch.jtaf.security;

import ch.jtaf.ui.DashboardView;
import ch.jtaf.ui.LoginView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

@Component
public class SecurityServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> uiEvent.getUI().addBeforeEnterListener(this::beforeEnter));
    }

    private void beforeEnter(BeforeEnterEvent event) {
        if (protectedRoute(event) && !SecurityContext.isUserLoggedIn()) {
            String redirect = event.getNavigationTarget().getSimpleName().substring(0, event.getNavigationTarget().getSimpleName().indexOf("View")).toLowerCase();
            event.rerouteTo("login", redirect);
        }
    }

    private boolean protectedRoute(BeforeEnterEvent event) {
        return !LoginView.class.equals(event.getNavigationTarget())
                && !DashboardView.class.equals(event.getNavigationTarget());
    }
}
