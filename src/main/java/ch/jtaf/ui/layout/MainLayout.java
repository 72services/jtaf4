package ch.jtaf.ui.layout;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.security.OrganizationHolder;
import ch.jtaf.security.SecurityContext;
import ch.jtaf.ui.view.AthletesView;
import ch.jtaf.ui.view.ClubsView;
import ch.jtaf.ui.view.DashboardView;
import ch.jtaf.ui.view.EventsView;
import ch.jtaf.ui.view.OrganizationsView;
import ch.jtaf.ui.view.SeriesListView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
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

@CssImport(value = "./styles/dialog-overlay.css", themeFor = "vaadin-dialog-overlay")
@PWA(name = "JTAF 4", shortName = "JTAF 4", description = "JTAF - Track and Field")
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    private static final long serialVersionUID = 1L;

    public static final String ICON_SIZE = "50px";

    private final Tabs tabsMainMenu = new Tabs();
    private final Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();

    private final Div version = new Div();
    private final Button signIn = new Button();

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
        tabsMainMenu.getStyle().set("height", "calc(100% - 30px)");

        Div divFooter = new Div();
        divFooter.setHeight("30px");
        divFooter.getStyle().set("position", "absolute");
        divFooter.getStyle().set("bottom", "20px");
        divFooter.getStyle().set("left", "20px");

        Anchor byLink = new Anchor();
        byLink.setWidth("300px");
        byLink.setText("by 72© Services LLC");
        byLink.setHref("https://72.services");
        byLink.setTarget("_blank");
        divFooter.add(byLink);

        Div divDrawer = new Div();
        divDrawer.setHeight("95%");
        divDrawer.add(tabsMainMenu);
        divDrawer.add(divFooter);

        addToDrawer(divDrawer);

        addToNavbar(new DrawerToggle());

        Image image = new Image("icons/icon.png", "JTAF");
        image.setMinWidth(ICON_SIZE);
        image.setWidth(ICON_SIZE);
        image.setMinHeight(ICON_SIZE);
        image.setHeight(ICON_SIZE);
        addToNavbar(image);


        H3 title = new H3("JTAF - Track and Field");
        title.setWidth("320px");
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
            if (SecurityContext.isUserLoggedIn()) {
                UI.getCurrent().getPage().setLocation(VaadinServlet.getCurrent().getServletContext().getContextPath() + "/logout");
            } else {
                UI.getCurrent().navigate(OrganizationsView.class);
            }
        });
        info.add(signIn);

        addToNavbar(info);
    }

    private void addMainMenu() {
        Tab tabDashboard = new Tab(new RouterLink(getTranslation("Dashboard"), DashboardView.class));
        navigationTargetToTab.put(DashboardView.class, tabDashboard);
        tabsMainMenu.add(tabDashboard);

        Tab tabOrganizaton = new Tab(new RouterLink(getTranslation("My.Organizations"), OrganizationsView.class));
        navigationTargetToTab.put(OrganizationsView.class, tabOrganizaton);
        tabsMainMenu.add(tabOrganizaton);

        seriesLink = new RouterLink("", SeriesListView.class);
        seriesLink.getStyle().set("font-size", "20px");
        tabSeries = new Tab(seriesLink);
        tabSeries.setVisible(false);
        navigationTargetToTab.put(SeriesListView.class, tabSeries);
        tabsMainMenu.add(tabSeries);

        tabEvents = new Tab(new RouterLink(getTranslation("Events"), EventsView.class));
        tabEvents.setVisible(false);
        navigationTargetToTab.put(EventsView.class, tabEvents);
        tabsMainMenu.add(tabEvents);

        tabClubs = new Tab(new RouterLink(getTranslation("Clubs"), ClubsView.class));
        tabClubs.setVisible(false);
        navigationTargetToTab.put(ClubsView.class, tabClubs);
        tabsMainMenu.add(tabClubs);

        tabAthletes = new Tab(new RouterLink(getTranslation("Athletes"), AthletesView.class));
        tabAthletes.setVisible(false);
        navigationTargetToTab.put(EventsView.class, tabAthletes);
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
            signIn.setText(getTranslation("Sign.out") + " (" + SecurityContextHolder.getContext().getAuthentication().getName() + ")");

            OrganizationRecord organization = OrganizationHolder.getOrganization();
            if (organization != null) {
                seriesLink.setText(organization.getOrganizationKey());
                setSeriesTabsVisible(true);
            }
        } else {
            signIn.setText(getTranslation("Sign.in"));
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

