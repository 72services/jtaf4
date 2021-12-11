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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Theme("jtaf")
@PWA(name = "JTAF 4", shortName = "JTAF 4", description = "JTAF - Track and Field")
@StyleSheet("https://fonts.googleapis.com/css2?family=Poppins")
public class MainLayout extends AppLayout implements BeforeEnterObserver, AppShellConfigurator {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Div version = new Div();
    private Button login;
    private Button logout;

    @Value("${application.version}")
    private String applicationVersion;

    private H1 viewTitle;

    private RouterLink seriesLink;
    private RouterLink eventsLink;
    private RouterLink clubsLink;
    private RouterLink athletesLink;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.setId("view-title");
        viewTitle.addClassNames("m-0", "text-l");
        viewTitle.setWidth("300px");

        HorizontalLayout info = new HorizontalLayout();
        info.setWidthFull();
        info.getStyle().set("padding-right", "20px");
        info.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        info.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        Anchor about = new Anchor("https://github.com/72services/jtaf4", "About");
        about.setTarget("_blank");

        login = new Button("Login", e -> UI.getCurrent().navigate(OrganizationsView.class));
        login.setVisible(false);

        logout = new Button("Logout", e -> {
            UI.getCurrent().getSession().close();
            UI.getCurrent().getPage().setLocation("/logout");
        });

        info.add(about, version, login, logout);

        Header header = new Header(toggle, viewTitle, info);
        header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center", "w-full");
        return header;
    }

    private Component createDrawerContent() {
        Image logo = new Image("icons/logo.png", "JTAF");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(logo, createNavigation(), createFooter());
        section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
        nav.getElement().setAttribute("aria-labelledby", "views");

        H3 views = new H3("Views");
        views.addClassNames("flex", "h-m", "items-center", "mx-m", "my-0", "text-s", "text-tertiary");
        views.setId("views");

        for (RouterLink link : createLinks()) {
            nav.add(link);
        }
        return nav;
    }

    private List<RouterLink> createLinks() {
        List<RouterLink> links = new ArrayList<>();

        links.add(createLink(new MenuItemInfo(getTranslation("Dashboard"), "la la-globe", DashboardView.class)));
        links.add(createLink(new MenuItemInfo(getTranslation("My.Organizations"), "la la-file", OrganizationsView.class)));

        seriesLink = createLink(new MenuItemInfo("", "la la-file", SeriesListView.class));
        links.add(seriesLink);

        eventsLink = createLink(new MenuItemInfo(getTranslation("Events"), "la la-file", EventsView.class));
        links.add(eventsLink);

        clubsLink = createLink(new MenuItemInfo(getTranslation("Clubs"), "la la-file", ClubsView.class));
        links.add(clubsLink);

        athletesLink = createLink(new MenuItemInfo(getTranslation("Athletes"), "la la-file", AthletesView.class));
        links.add(athletesLink);

        setVisibilityOfLinks(false);

        return links;
    }

    private static RouterLink createLink(MenuItemInfo menuItemInfo) {
        RouterLink link = new RouterLink();
        link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
        link.setRoute(menuItemInfo.getView());

        Span icon = new Span();
        icon.addClassNames("me-s", "text-l");
        if (!menuItemInfo.getIconClass().isEmpty()) {
            icon.addClassNames(menuItemInfo.getIconClass());
        }

        Span text = new Span(menuItemInfo.getText());
        text.addClassNames("font-medium", "text-s");

        link.add(icon, text);
        return link;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("flex", "my-s", "px-m", "py-xs");

        Html htmlByLink = new Html("<p style='color: var(--lumo-primary-color)'>Free and<br>Open Source<br>by 72© Services LLC</p>");

        Anchor byLink = new Anchor();
        byLink.setWidth("300px");
        byLink.getElement().getStyle().set("font-size", "small");
        byLink.setHref("https://72.services");
        byLink.setTarget("_blank");
        byLink.add(htmlByLink);
        layout.add(byLink);

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        HasDynamicTitle title = (HasDynamicTitle) getContent();
        return title == null ? "" : title.getPageTitle();
    }

    @PostConstruct
    public void postConstruct() {
        version.setText(applicationVersion);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityContext.isUserLoggedIn()) {
            login.setVisible(false);
            logout.setVisible(true);

            OrganizationRecord organization = OrganizationHolder.getOrganization();
            if (organization != null) {
                seriesLink.setText(organization.getOrganizationKey());
                setVisibilityOfLinks(true);
            }
        } else {
            login.setVisible(true);
            logout.setVisible(false);

            seriesLink.setText("");
            setVisibilityOfLinks(false);
        }
    }

    private void setVisibilityOfLinks(boolean visible) {
        seriesLink.setVisible(visible);
        eventsLink.setVisible(visible);
        clubsLink.setVisible(visible);
        athletesLink.setVisible(visible);
    }

    public static class MenuItemInfo {

        private String text;
        private String iconClass;
        private Class<? extends Component> view;

        public MenuItemInfo(String text, String iconClass, Class<? extends Component> view) {
            this.text = text;
            this.iconClass = iconClass;
            this.view = view;
        }

        public String getText() {
            return text;
        }

        public String getIconClass() {
            return iconClass;
        }

        public Class<? extends Component> getView() {
            return view;
        }

    }
}
