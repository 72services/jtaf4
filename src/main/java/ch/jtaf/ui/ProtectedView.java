package ch.jtaf.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "protected", layout = MainLayout.class)
public class ProtectedView extends VerticalLayout {

    public ProtectedView() {
        add(new H1("Protected"));
    }

}
