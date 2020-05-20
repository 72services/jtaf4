package ch.jtaf.ui.view;

import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

@Route(layout = MainLayout.class)
public class ResultCapturingView extends HorizontalLayout implements HasDynamicTitle {

    public ResultCapturingView(DSLContext dsl) {
        add(new H1(getTranslation("Enter.Results")));
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Enter.Results");
    }
}
