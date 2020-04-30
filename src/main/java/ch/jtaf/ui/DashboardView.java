package ch.jtaf.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.jooq.DSLContext;

import java.io.ByteArrayInputStream;

import static ch.jtaf.db.tables.Series.SERIES;

@PageTitle("JTAF - Dashboard")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    public DashboardView(DSLContext dsl) {
        add(new H1("Dashboard"));

        var seriesList = dsl
                .selectFrom(SERIES)
                .fetch();

        seriesList.forEach(seriesRecord -> {
            add(new H2(seriesRecord.getName()));

            Image logo = new Image();
            logo.setSrc(new StreamResource("logo", () -> new ByteArrayInputStream(seriesRecord.getLogo())));
            logo.setWidth("60px");
            add(logo);
        });
    }

}
