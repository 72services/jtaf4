package ch.jtaf.ui;

import ch.jtaf.security.SecurityContext;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;

import java.util.HashMap;
import java.util.Map;

@PWA(name = "JTAF 4", shortName = "JTAF 4", description = "JTAF - Track and Field")
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private Tabs tabs = new Tabs();
    private Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();

    private Div userInfo = new Div();

    public MainLayout() {
        addMenuTab("Dashboard", DashboardView.class);
        addMenuTab("Protected", ProtectedView.class);
        tabs.setOrientation(Tabs.Orientation.VERTICAL);

        addToDrawer(tabs);

        addToNavbar(new DrawerToggle());

        userInfo.getStyle().set("cursor", "pointer");
        userInfo.addClickListener(clickEvent -> {
            if (SecurityContext.isUserLoggedIn()) {
                UI.getCurrent().getPage().executeJs("window.location.href='/'");
                UI.getCurrent().getSession().close();
            } else {
                UI.getCurrent().navigate(ProtectedView.class);
            }
        });


        HorizontalLayout navbar = new HorizontalLayout();
        navbar.setWidthFull();
        navbar.getStyle().set("padding-right", "20px");
        navbar.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        navbar.add(userInfo);

        addToNavbar(navbar);
    }

    private void addMenuTab(String label, Class<? extends Component> target) {
        Tab tab = new Tab(new RouterLink(label, target));
        navigationTargetToTab.put(target, tab);
        tabs.add(tab);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        tabs.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));

        if (SecurityContext.isUserLoggedIn()) {
            userInfo.setText("Logout (" + SecurityContext.getUser().getFirstName() + " " + SecurityContext.getUser().getLastName() + ")");
        } else {
            userInfo.setText("Login");
        }
    }
}
