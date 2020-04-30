package ch.jtaf.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import org.jooq.DSLContext;

import static ch.jtaf.db.tables.Series.SERIES;

@Route
@PWA(name = "JTAF 4", shortName = "JTAF 4", description = "JTAF - Track and Field")
public class MainView extends VerticalLayout {

    public MainView(DSLContext dsl) {
        add(new H1("JTAF - Track and Field"));

        var series = dsl
                .selectFrom(SERIES)
                .orderBy(SERIES.NAME)
                .fetch();

        series.forEach(seriesRecord -> {
            add(new Label(seriesRecord.getName()));
        });


        add(new RouterLink("secure", ProtectedView.class));
    }

}
