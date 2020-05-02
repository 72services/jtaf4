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

    private final DSLContext dsl;

    public ResultCapturingView(DSLContext dsl, TransactionTemplate transactionTemplate) {
        this.dsl = dsl;

        add(new H1(getTranslation("Enter.Results")));
    }

}
