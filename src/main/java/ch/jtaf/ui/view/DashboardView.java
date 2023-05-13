package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.SecurityContext;
import ch.jtaf.service.CompetitionRankingService;
import ch.jtaf.service.SeriesRankingService;
import ch.jtaf.ui.layout.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.ByteArrayInputStream;
import java.io.Serial;

import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Series.SERIES;
import static ch.jtaf.ui.util.LogoUtil.resizeLogo;

@AnonymousAllowed
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements HasDynamicTitle {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String NAME_MIN_WIDTH = "350px";

    private final Anchor downloadWidget;

    public DashboardView(DSLContext dsl, SeriesRankingService seriesRankingService, CompetitionRankingService competitionRankingService) {
        downloadWidget = new Anchor();
        downloadWidget.getStyle().set("display", "none");
        downloadWidget.setTarget("_blank");
        add(downloadWidget);

        getClassNames().add("dashboard");

        var verticalLayout = new VerticalLayout();
        add(verticalLayout);

        int seriesIndex = 1;
        var seriesRecords = dsl.selectFrom(SERIES)
            .orderBy(DSL.field(dsl.select(DSL.max(COMPETITION.COMPETITION_DATE)).from(COMPETITION).where(COMPETITION.SERIES_ID.eq(SERIES.ID))).desc())
            .fetch();
        for (var series : seriesRecords) {
            HorizontalLayout seriesLayout = new HorizontalLayout();
            seriesLayout.getClassNames().add("series-layout");
            verticalLayout.add(seriesLayout);

            var logo = resizeLogo(series);
            var divLogo = new Div(logo);
            divLogo.setWidth("100px");
            seriesLayout.add(divLogo);

            var pSeriesName = new Paragraph(series.getName());
            pSeriesName.setMinWidth("270px");
            seriesLayout.add(pSeriesName);

            var buttonLayout = new HorizontalLayout();
            buttonLayout.getClassNames().add("button-layout");
            seriesLayout.add(buttonLayout);

            var seriesRanking = new Button(getTranslation("Series.Ranking"), new Icon(VaadinIcon.FILE));
            seriesRanking.setId("series-ranking-" + seriesIndex);
            seriesRanking.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            seriesRanking.addClickListener(event -> {
                StreamResource streamResource = new StreamResource("series_ranking" + series.getId() + ".pdf",
                    () -> {
                        var pdf = seriesRankingService.getSeriesRankingAsPdf(series.getId());
                        return new ByteArrayInputStream(pdf);
                    });
                download(streamResource);
            });
            var seriesRankingDiv = new Div(seriesRanking);
            buttonLayout.add(seriesRankingDiv);

            var clubRanking = new Button(getTranslation("Club.Ranking"), new Icon(VaadinIcon.FILE));
            clubRanking.setId("club-ranking-" + seriesIndex);
            clubRanking.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            clubRanking.addClickListener(event -> {
                StreamResource streamResource = new StreamResource("club_ranking" + series.getId() + ".pdf",
                    () -> {
                        byte[] pdf = seriesRankingService.getClubRankingAsPdf(series.getId());
                        return new ByteArrayInputStream(pdf);
                    });
                download(streamResource);
            });
            var clubRankingDiv = new Div(clubRanking);
            buttonLayout.add(clubRankingDiv);

            int competitionIndex = 1;
            var competitionRecords = dsl.selectFrom(COMPETITION)
                .where(COMPETITION.SERIES_ID.eq(series.getId()))
                .fetch();
            for (var competition : competitionRecords) {
                var competitionLayout = new HorizontalLayout();
                competitionLayout.getClassNames().add("competition-layout");
                competitionLayout.setWidthFull();
                verticalLayout.add(competitionLayout);

                var fakeLogo = new Paragraph();
                fakeLogo.setWidth("100px");
                competitionLayout.add(fakeLogo);

                var pCompetition = new Paragraph("%s %s".formatted(competition.getName(), competition.getCompetitionDate()));
                pCompetition.setMinWidth(NAME_MIN_WIDTH);
                competitionLayout.add(pCompetition);

                var links = new HorizontalLayout();
                links.getClassNames().add("links-layout");
                competitionLayout.add(links);

                var competitionRanking = new Button(getTranslation("Competition.Ranking"), new Icon(VaadinIcon.FILE));
                competitionRanking.setId("competition-ranking-" + seriesIndex + "-" + competitionIndex);
                competitionRanking.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                competitionRanking.addClickListener(event -> {
                    StreamResource streamResource = new StreamResource("competition_ranking" + competition.getId() + ".pdf",
                        () -> {
                            byte[] pdf = competitionRankingService.getCompetitionRankingAsPdf(competition.getId());
                            return new ByteArrayInputStream(pdf);
                        });
                    download(streamResource);
                });
                var competitionRankingDiv = new Div(competitionRanking);
                links.add(competitionRankingDiv);

                if (SecurityContext.isUserLoggedIn()) {
                    var diploma = new Button(getTranslation("Diploma"), new Icon(VaadinIcon.FILE));
                    diploma.setId("diploma-" + seriesIndex + "-" + competitionIndex);
                    diploma.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                    diploma.addClickListener(event -> {
                        var streamResource = new StreamResource("diploma" + competition.getId() + ".pdf",
                            () -> {
                                var pdf = competitionRankingService.getDiplomasAsPdf(competition.getId());
                                return new ByteArrayInputStream(pdf);
                            });
                        download(streamResource);
                    });
                    var diplomaDiv = new Div(diploma);
                    links.add(diplomaDiv);

                    var eventRanking = new Button(getTranslation("Event.Ranking"), new Icon(VaadinIcon.FILE));
                    eventRanking.setId("event-ranking-" + seriesIndex + "-" + competitionIndex);
                    eventRanking.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                    eventRanking.addClickListener(event -> {
                        StreamResource streamResource = new StreamResource("event_ranking" + competition.getId() + ".pdf",
                            () -> {
                                var pdf = competitionRankingService.getEventRankingAsPdf(competition.getId());
                                return new ByteArrayInputStream(pdf);
                            });
                        download(streamResource);
                    });
                    var eventRankingDiv = new Div(eventRanking);
                    links.add(eventRankingDiv);

                    var enterResults = new Button(getTranslation("Enter.Results"), new Icon(VaadinIcon.KEYBOARD));
                    enterResults.setId("enter-results-" + seriesIndex + "-" + competitionIndex);
                    enterResults.addThemeVariants(ButtonVariant.LUMO_ERROR);
                    enterResults.addClickListener(event -> UI.getCurrent().navigate(ResultCapturingView.class, competition.getId().toString()));
                    var enterResultsDiv = new Div(enterResults);
                    links.add(enterResultsDiv);

                    competitionIndex++;
                }
            }
            Hr hr = new Hr();
            hr.setClassName("dashboard-separator");
            verticalLayout.add(hr);

            seriesIndex++;
        }
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Dashboard");
    }

    protected void download(StreamResource resource) {
        downloadWidget.setHref(resource);
        UI.getCurrent().getPage().executeJs("$0.click();", downloadWidget.getElement());
    }
}
