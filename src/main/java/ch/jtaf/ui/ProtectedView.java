package ch.jtaf.ui;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@Route
public class ProtectedView extends VerticalLayout {

    public ProtectedView() {
        add(new H1("Secure"));
    }

}
