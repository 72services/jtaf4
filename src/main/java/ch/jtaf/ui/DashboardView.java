package ch.jtaf.ui;

import ch.jtaf.db.tables.records.SeriesRecord;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Series.SERIES;

@PageTitle("JTAF - Dashboard")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    public DashboardView(DSLContext dsl) {
        add(new H1("Dashboard"));


        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidthFull();
        add(verticalLayout);

        dsl.selectFrom(SERIES)
                .orderBy(DSL.field(dsl.select(DSL.max(COMPETITION.COMPETITION_DATE)).from(COMPETITION).where(COMPETITION.SERIES_ID.eq(SERIES.ID))).desc())
                .fetch()
                .forEach(series -> {
                    HorizontalLayout seriesLayout = new HorizontalLayout();
                    verticalLayout.add(seriesLayout);

                    Image logo = resizeLogo(series);
                    seriesLayout.add(logo);
                    seriesLayout.add(new H2(series.getName()));

                    dsl.selectFrom(COMPETITION)
                            .where(COMPETITION.SERIES_ID.eq(series.getId()))
                            .fetch()
                            .forEach(competition -> verticalLayout.add(new Paragraph(competition.getName() + " " + competition.getCompetitionDate())));
                });
    }

    private Image resizeLogo(SeriesRecord series) {
        Image logo = new Image();

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(series.getLogo()));
            double width = image.getWidth(null);
            double height = image.getHeight(null);
            double ratio = width / height;

            logo.setSrc(new StreamResource("logo", () -> new ByteArrayInputStream(series.getLogo())));
            logo.setHeight("60px");
            logo.setWidth(60 * ratio + "px");
        } catch (IOException e) {
            // Ignore
        }
        return logo;
    }

}
