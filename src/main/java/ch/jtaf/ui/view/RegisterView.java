package ch.jtaf.ui.view;

import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;

@Route(value = "register", layout = MainLayout.class)
public class RegisterView extends Div implements HasDynamicTitle {

    public RegisterView() {
        H1 h1 = new H1();
        h1.setText(getTranslation("Register"));
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Register");
    }
}
