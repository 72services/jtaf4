package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.service.CompetitionRankingService;
import ch.jtaf.service.SeriesRankingService;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
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

    public DashboardView(DSLContext dsl, SeriesRankingService seriesRankingService, CompetitionRankingService competitionRankingService) {
        add(new H1("Dashboard"));

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidthFull();
        add(verticalLayout);

        dsl.selectFrom(SERIES)
                .orderBy(DSL.field(dsl.select(DSL.max(COMPETITION.COMPETITION_DATE)).from(COMPETITION).where(COMPETITION.SERIES_ID.eq(SERIES.ID))).desc())
                .fetch()
                .forEach(series -> {
                    HorizontalLayout seriesLayout = new HorizontalLayout();
                    seriesLayout.setDefaultVerticalComponentAlignment(Alignment.END);

                    verticalLayout.add(seriesLayout);

                    Image logo = resizeLogo(series);
                    Div divLogo = new Div(logo);
                    divLogo.setWidth("100px");
                    seriesLayout.add(divLogo);

                    H2 h2SeriesName = new H2(series.getName());
                    h2SeriesName.setWidth("400px");
                    seriesLayout.add(h2SeriesName);

                    Anchor seriesRanking = new Anchor(new StreamResource("series_ranking" + series.getId() + ".pdf",
                            () -> {
                                byte[] pdf = seriesRankingService.getSeriesRankingAsPdf(series.getId());
                                return new ByteArrayInputStream(pdf);
                            }), "Series Ranking");
                    seriesRanking.getElement().setAttribute("download", true);

                    seriesLayout.add(new Paragraph(seriesRanking));

                    dsl.selectFrom(COMPETITION)
                            .where(COMPETITION.SERIES_ID.eq(series.getId()))
                            .fetch()
                            .forEach(competition -> {
                                HorizontalLayout competionLayout = new HorizontalLayout();
                                verticalLayout.add(competionLayout);

                                Paragraph pCompetition = new Paragraph(competition.getName() + " " + competition.getCompetitionDate());
                                pCompetition.setWidth("515px");
                                competionLayout.add(pCompetition);

                                Anchor competitionRanking = new Anchor(new StreamResource("competition_ranking" + competition.getId() + ".pdf",
                                        () -> {
                                            byte[] pdf = competitionRankingService.getCompetitionRankingAsPdf(competition.getId());
                                            return new ByteArrayInputStream(pdf);
                                        }), "Competition Ranking");
                                competitionRanking.getElement().setAttribute("download", true);

                                competionLayout.add(new Paragraph(competitionRanking));
                            });
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
