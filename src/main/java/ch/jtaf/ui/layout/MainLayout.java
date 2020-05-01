package ch.jtaf.ui.layout;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.view.DashboardView;
import ch.jtaf.ui.view.OrganizationView;
import ch.jtaf.ui.view.SeriesView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinServlet;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

@PWA(name = "JTAF 4", shortName = "JTAF 4", description = "JTAF - Track and Field")
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private final Tabs tabsMainMenu = new Tabs();
    private final Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();

    private final Div user = new Div();
    private final H3 organizationTitle = new H3();
    private Tab tabSeries;

    public MainLayout() {
        addMainMenu();
        tabsMainMenu.setOrientation(Tabs.Orientation.VERTICAL);

        addToDrawer(tabsMainMenu);

        addToNavbar(new DrawerToggle());

        user.getStyle().set("cursor", "pointer");
        user.addClickListener(clickEvent -> {
            if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                UI.getCurrent().getPage().setLocation(VaadinServlet.getCurrent().getServletContext().getContextPath() + "/logout");
            } else {
                UI.getCurrent().navigate(OrganizationView.class);
            }
        });

        Paragraph title = new Paragraph("JTAF - Track and Field");
        title.setWidth("400px");
        addToNavbar(title);

        HorizontalLayout info = new HorizontalLayout();
        info.setWidthFull();
        info.getStyle().set("padding-right", "20px");
        info.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        info.add(user);

        addToNavbar(info);
    }

    private void addMainMenu() {
        Tab tabDashboard = new Tab(new RouterLink("Dashboard", DashboardView.class));
        navigationTargetToTab.put(DashboardView.class, tabDashboard);
        tabsMainMenu.add(tabDashboard);

        Tab tabOrganizaton = new Tab(new RouterLink("My Organizations", OrganizationView.class));
        navigationTargetToTab.put(OrganizationView.class, tabOrganizaton);
        tabsMainMenu.add(tabOrganizaton);

        organizationTitle.setVisible(false);
        tabsMainMenu.add(organizationTitle);

        tabSeries = new Tab(new RouterLink("Series", SeriesView.class));
        tabSeries.setVisible(false);
        navigationTargetToTab.put(SeriesView.class, tabSeries);
        tabsMainMenu.add(tabSeries);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        tabsMainMenu.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));

        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getName().equalsIgnoreCase("anonymousUser")) {
            user.setText("Logout (" + SecurityContextHolder.getContext().getAuthentication().getName() + ")");

            OrganizationRecord organization = UI.getCurrent().getSession().getAttribute(OrganizationRecord.class);
            if (organization != null) {
                organizationTitle.setText(organization.getOrganizationKey());
                organizationTitle.setVisible(true);
                tabSeries.setVisible(true);
            }
        } else {
            user.setText("Login");

            organizationTitle.setVisible(false);
            tabSeries.setVisible(false);
        }
    }
}
