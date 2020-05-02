package ch.jtaf.ui.view;

import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jooq.DSLContext;
import org.springframework.transaction.support.TransactionTemplate;

@PageTitle("JTAF - Series")
@Route(layout = MainLayout.class)
public class ResultCapturingView extends VerticalLayout {

    public ResultCapturingView(DSLContext dsl, TransactionTemplate transactionTemplate) {

        add(new H1(getTranslation("Enter.Results")));
    }

}
