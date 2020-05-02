package ch.jtaf.ui.layout;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.security.SecurityUtil;
import ch.jtaf.ui.view.DashboardView;
import ch.jtaf.ui.view.OrganizationView;
import ch.jtaf.ui.view.SeriesView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.VaadinServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@PWA(name = "JTAF 4", shortName = "JTAF 4", description = "JTAF - Track and Field")
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    public static final String ICON_SIZE = "50px";

    private final Tabs tabsMainMenu = new Tabs();
    private final Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();

    private final Div version = new Div();
    private final Button signIn = new Button();
    private final H3 organizationTitle = new H3();

    private Tab tabSeries;

    @Value("${application.version}")
    private String applicationVersion;

    public MainLayout() {
        addMainMenu();
        tabsMainMenu.setOrientation(Tabs.Orientation.VERTICAL);

        addToDrawer(tabsMainMenu);

        addToNavbar(new DrawerToggle());

        Image image = new Image("icons/icon.png", "JTAF");
        image.setMinWidth(ICON_SIZE);
        image.setWidth(ICON_SIZE);
        image.setMinHeight(ICON_SIZE);
        image.setHeight(ICON_SIZE);
        addToNavbar(image);

        H3 title = new H3("JTAF - Track and Field");
        title.setWidth("400px");
        title.getStyle().set("padding-left", "20px");
        addToNavbar(title);

        HorizontalLayout info = new HorizontalLayout();
        info.setWidthFull();
        info.getStyle().set("padding-right", "20px");
        info.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        info.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        version.getStyle().set("padding-right", "50px");
        info.add(version);

        signIn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        signIn.addClickListener(clickEvent -> {
            if (SecurityUtil.isUserLoggedIn()) {
                UI.getCurrent().getPage().setLocation(VaadinServlet.getCurrent().getServletContext().getContextPath() + "/logout");
            } else {
                UI.getCurrent().navigate(OrganizationView.class);
            }
        });
        info.add(signIn);

        addToNavbar(info);
    }

    private void addMainMenu() {
        Tab tabDashboard = new Tab(new RouterLink(getTranslation("Dashboard"), DashboardView.class));
        navigationTargetToTab.put(DashboardView.class, tabDashboard);
        tabsMainMenu.add(tabDashboard);

        Tab tabOrganizaton = new Tab(new RouterLink(getTranslation("My.Organizations"), OrganizationView.class));
        navigationTargetToTab.put(OrganizationView.class, tabOrganizaton);
        tabsMainMenu.add(tabOrganizaton);

        organizationTitle.setVisible(false);
        tabsMainMenu.add(organizationTitle);

        tabSeries = new Tab(new RouterLink(getTranslation("Series"), SeriesView.class));
        tabSeries.setVisible(false);
        navigationTargetToTab.put(SeriesView.class, tabSeries);
        tabsMainMenu.add(tabSeries);
    }

    @PostConstruct
    public void postConstruct() {
        version.setText(applicationVersion);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        tabsMainMenu.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));

        if (SecurityUtil.isUserLoggedIn()) {
            signIn.setText(getTranslation("Sign.out") + " (" + SecurityContextHolder.getContext().getAuthentication().getName() + ")");

            OrganizationRecord organization = UI.getCurrent().getSession().getAttribute(OrganizationRecord.class);
            if (organization != null) {
                organizationTitle.setText(organization.getOrganizationKey());
                organizationTitle.setVisible(true);
                tabSeries.setVisible(true);
            }
        } else {
            signIn.setText(getTranslation("Sign.in"));

            organizationTitle.setVisible(false);
            tabSeries.setVisible(false);
        }
    }
}

