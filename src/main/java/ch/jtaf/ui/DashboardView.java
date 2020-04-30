package ch.jtaf.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;

@Route("")
@PWA(name = "JTAF 4", shortName = "JTAF 4", description = "JTAF - Track and Field")
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        add(new H1("JTAF - Track and Field"));

        add(new RouterLink("secure", ProtectedView.class));
    }

}
