package ch.jtaf.ui.view;

import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Series.SERIES;
import static ch.jtaf.util.LogoUtil.resizeLogo;

import java.io.ByteArrayInputStream;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;

import ch.jtaf.security.SecurityContext;
import ch.jtaf.service.CompetitionRankingService;
import ch.jtaf.service.SeriesRankingService;
import ch.jtaf.ui.layout.MainLayout;

@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements HasDynamicTitle {
	
	private static final long serialVersionUID = 1L;

    public DashboardView(DSLContext dsl, SeriesRankingService seriesRankingService, CompetitionRankingService competitionRankingService) {
        add(new H1(getTranslation("Dashboard")));

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
                            }), getTranslation("Series.Ranking"));
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
                                        }), getTranslation("Competition.Ranking"));
                                competitionRanking.getElement().setAttribute("download", true);

                                HorizontalLayout links = new HorizontalLayout(competitionRanking);

                                if (SecurityContext.isUserLoggedIn()) {
                                    RouterLink enterResults = new RouterLink(getTranslation("Enter.Results"),
                                            ResultCapturingView.class, competition.getId().toString());
                                    links.add(enterResults);
                                }

                                competionLayout.add(links);
                            });
                });
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Dashboard");
    }
}
