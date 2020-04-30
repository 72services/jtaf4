package ch.jtaf.security;

import ch.jtaf.ui.DashboardView;
import ch.jtaf.ui.LoginView;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> uiEvent.getUI().addBeforeEnterListener(this::beforeEnter));
    }

    /**
     * Reroutes the user if not authorized to access the view.
     *
     * @param event before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {
        if (protectedRoute(event) && !SecurityUtils.isUserLoggedIn()) {
            event.rerouteTo(LoginView.class);
        }
    }

    private boolean protectedRoute(BeforeEnterEvent event) {
        return !LoginView.class.equals(event.getNavigationTarget())
                && !DashboardView.class.equals(event.getNavigationTarget());
    }
}
