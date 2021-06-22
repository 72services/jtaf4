package ch.jtaf.ui.layout;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.security.OrganizationHolder;
import ch.jtaf.ui.security.SecurityContext;
import ch.jtaf.ui.view.AthletesView;
import ch.jtaf.ui.view.ClubsView;
import ch.jtaf.ui.view.DashboardView;
import ch.jtaf.ui.view.EventsView;
import ch.jtaf.ui.view.OrganizationsView;
import ch.jtaf.ui.view.SeriesListView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@CssImport(value = "./styles/dialog-overlay.css", themeFor = "vaadin-dialog-overlay")
@CssImport("./styles/jtaf.css")
@PWA(name = "JTAF 4", shortName = "JTAF 4", description = "JTAF - Track and Field")
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private static final long serialVersionUID = 1L;

    private static final String LOGO_SIZE = "50px";
    private static final String NAVIGATION_ICON = "navigation-icon";

    private final Tabs tabsMainMenu = new Tabs();
    private final Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();

    private final Div version = new Div();
    private final RouterLink login;
    private final Anchor logout;

    @Value("${application.version}")
    private String applicationVersion;

    private Tab tabSeries;
    private Tab tabEvents;
    private Tab tabClubs;
    private Tab tabAthletes;
    private RouterLink seriesLink;

    public MainLayout() {
        addMainMenu();

        tabsMainMenu.setOrientation(Tabs.Orientation.VERTICAL);
        tabsMainMenu.setHeightFull();

        Div divFooter = new Div();
        divFooter.setHeight("50px");
        divFooter.getStyle().set("position", "absolute");
        divFooter.getStyle().set("bottom", "20px");
        divFooter.getStyle().set("left", "20px");

        Html htmlByLink = new Html("<p>JTAF is Free and Open Source<br>by 72© Services LLC</p>");

        Anchor byLink = new Anchor();
        byLink.setWidth("300px");
        byLink.getElement().getStyle().set("font-size", "small");
        byLink.setHref("https://72.services");
        byLink.setTarget("_blank");
        byLink.add(htmlByLink);
        divFooter.add(byLink);

        Div divDrawer = new Div();
        divDrawer.setHeight("95%");
        divDrawer.add(tabsMainMenu);
        divDrawer.add(divFooter);

        addToDrawer(divDrawer);

        addToNavbar(new DrawerToggle());

        Image image = new Image("icons/icon.png", "JTAF");
        image.setMinWidth(LOGO_SIZE);
        image.setWidth(LOGO_SIZE);
        image.setMinHeight(LOGO_SIZE);
        image.setHeight(LOGO_SIZE);
        addToNavbar(image);


        H3 title = new H3("JTAF - Track and Field");
        title.setWidth("320px");
        addToNavbar(title);

        HorizontalLayout info = new HorizontalLayout();
        info.setWidthFull();
        info.getStyle().set("padding-right", "20px");
        info.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        info.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Anchor about = new Anchor("https://github.com/72services/jtaf4", "About");
        about.setTarget("_blank");
        info.add(about);

        info.add(version);

        login = new RouterLink("Login", OrganizationsView.class);
        login.setVisible(false);

        logout = new Anchor("/logout", "Logout");

        info.add(login, logout);

        addToNavbar(info);
    }

    private void addMainMenu() {
        Icon iconDashboard = new Icon(VaadinIcon.DASHBOARD);
        iconDashboard.setClassName(NAVIGATION_ICON);
        RouterLink dashboard = new RouterLink(getTranslation("Dashboard"), DashboardView.class);
        dashboard.addComponentAsFirst(iconDashboard);

        Tab tabDashboard = new Tab(dashboard);
        navigationTargetToTab.put(DashboardView.class, tabDashboard);
        tabsMainMenu.add(tabDashboard);

        Icon iconOrganizations = new Icon(VaadinIcon.WORKPLACE);
        iconOrganizations.setClassName(NAVIGATION_ICON);
        RouterLink organizations = new RouterLink(getTranslation("My.Organizations"), OrganizationsView.class);
        organizations.addComponentAsFirst(iconOrganizations);

        Tab tabOrganization = new Tab(organizations);
        navigationTargetToTab.put(OrganizationsView.class, tabOrganization);
        tabsMainMenu.add(tabOrganization);

        Tab tabEmpty = new Tab();
        tabEmpty.setEnabled(false);
        navigationTargetToTab.put(null, tabEmpty);
        tabsMainMenu.add(tabEmpty);

        seriesLink = new RouterLink("", SeriesListView.class);
        seriesLink.getStyle().set("font-size", "20px");

        tabSeries = new Tab(seriesLink);
        tabSeries.setVisible(false);
        navigationTargetToTab.put(SeriesListView.class, tabSeries);
        tabsMainMenu.add(tabSeries);

        Icon iconEvents = new Icon(VaadinIcon.LIST_OL);
        iconEvents.setClassName(NAVIGATION_ICON);
        RouterLink events = new RouterLink(getTranslation("Events"), EventsView.class);
        events.addComponentAsFirst(iconEvents);

        tabEvents = new Tab(events);
        tabEvents.setVisible(false);
        navigationTargetToTab.put(EventsView.class, tabEvents);
        tabsMainMenu.add(tabEvents);

        Icon iconClubs = new Icon(VaadinIcon.GROUP);
        iconClubs.setClassName(NAVIGATION_ICON);
        RouterLink clubs = new RouterLink(getTranslation("Clubs"), ClubsView.class);
        clubs.addComponentAsFirst(iconClubs);

        tabClubs = new Tab(clubs);
        tabClubs.setVisible(false);
        navigationTargetToTab.put(ClubsView.class, tabClubs);
        tabsMainMenu.add(tabClubs);

        Icon iconAthletes = new Icon(VaadinIcon.FAMILY);
        iconAthletes.setClassName(NAVIGATION_ICON);
        RouterLink athletes = new RouterLink(getTranslation("Athletes"), AthletesView.class);
        athletes.addComponentAsFirst(iconAthletes);

        tabAthletes = new Tab(athletes);
        tabAthletes.setVisible(false);
        navigationTargetToTab.put(AthletesView.class, tabAthletes);
        tabsMainMenu.add(tabAthletes);
    }

    @PostConstruct
    public void postConstruct() {
        version.setText(applicationVersion);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        tabsMainMenu.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));

        if (SecurityContext.isUserLoggedIn()) {
            login.setVisible(false);
            logout.setVisible(true);

            OrganizationRecord organization = OrganizationHolder.getOrganization();
            if (organization != null) {
                seriesLink.setText(organization.getOrganizationKey());
                setSeriesTabsVisible(true);
            }
        } else {
            login.setVisible(true);
            logout.setVisible(false);

            seriesLink.setText("");
            setSeriesTabsVisible(false);
        }
    }

    private void setSeriesTabsVisible(boolean visible) {
        tabSeries.setVisible(visible);
        tabEvents.setVisible(visible);
        tabClubs.setVisible(visible);
        tabAthletes.setVisible(visible);
    }
}
