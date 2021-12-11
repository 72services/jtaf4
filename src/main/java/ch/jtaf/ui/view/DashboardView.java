package ch.jtaf.ui.view;

import ch.jtaf.service.CompetitionRankingService;
import ch.jtaf.service.SeriesRankingService;
import ch.jtaf.ui.layout.MainLayout;
import ch.jtaf.ui.security.SecurityContext;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.ByteArrayInputStream;
import java.io.Serial;

import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Series.SERIES;
import static ch.jtaf.util.LogoUtil.resizeLogo;

@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements HasDynamicTitle {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Anchor downloadWidget;

    public DashboardView(DSLContext dsl, SeriesRankingService seriesRankingService, CompetitionRankingService competitionRankingService) {
        downloadWidget = new Anchor();
        downloadWidget.getStyle().set("display", "none");
        downloadWidget.setTarget("_blank");
        add(downloadWidget);

        getClassNames().add("dashboard");

        VerticalLayout verticalLayout = new VerticalLayout();
        add(verticalLayout);

        dsl.selectFrom(SERIES)
            .orderBy(DSL.field(dsl.select(DSL.max(COMPETITION.COMPETITION_DATE)).from(COMPETITION).where(COMPETITION.SERIES_ID.eq(SERIES.ID))).desc())
            .fetch()
            .forEach(series -> {
                HorizontalLayout seriesLayout = new HorizontalLayout();
                seriesLayout.getClassNames().add("series-layout");
                seriesLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

                verticalLayout.add(seriesLayout);

                Image logo = resizeLogo(series);
                Div divLogo = new Div(logo);
                divLogo.setWidth("100px");
                seriesLayout.add(divLogo);

                Paragraph pSeriesName = new Paragraph(series.getName());
                pSeriesName.setWidth("400px");
                seriesLayout.add(pSeriesName);

                HorizontalLayout buttonLayout = new HorizontalLayout();
                seriesLayout.add(buttonLayout);

                Button seriesRanking = new Button(getTranslation("Series.Ranking"), new Icon(VaadinIcon.FILE));
                seriesRanking.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                seriesRanking.addClickListener(event -> {
                    StreamResource streamResource = new StreamResource("series_ranking" + series.getId() + ".pdf",
                        () -> {
                            byte[] pdf = seriesRankingService.getSeriesRankingAsPdf(series.getId());
                            return new ByteArrayInputStream(pdf);
                        });
                    download(streamResource);
                });
                Div seriesRankingDiv = new Div(seriesRanking);
                seriesRankingDiv.getClassNames().add("button-div");
                buttonLayout.add(seriesRankingDiv);

                Button clubRanking = new Button(getTranslation("Club.Ranking"), new Icon(VaadinIcon.FILE));
                clubRanking.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                clubRanking.addClickListener(event -> {
                    StreamResource streamResource = new StreamResource("club_ranking" + series.getId() + ".pdf",
                        () -> {
                            byte[] pdf = seriesRankingService.getClubRankingAsPdf(series.getId());
                            return new ByteArrayInputStream(pdf);
                        });
                    download(streamResource);
                });
                Div clubRankingDiv = new Div(clubRanking);
                clubRankingDiv.getClassNames().add("button-div");
                buttonLayout.add(clubRankingDiv);

                dsl.selectFrom(COMPETITION)
                    .where(COMPETITION.SERIES_ID.eq(series.getId()))
                    .fetch()
                    .forEach(competition -> {
                        HorizontalLayout competitionLayout = new HorizontalLayout();
                        competitionLayout.getClassNames().add("competition-layout");
                        competitionLayout.setWidthFull();
                        verticalLayout.add(competitionLayout);

                        Paragraph fakeLogo = new Paragraph();
                        fakeLogo.setWidth("100px");
                        competitionLayout.add(fakeLogo);

                        Paragraph pCompetition = new Paragraph(competition.getName() + " " + competition.getCompetitionDate());
                        pCompetition.setWidth("400px");
                        competitionLayout.add(pCompetition);

                        HorizontalLayout links = new HorizontalLayout();
                        competitionLayout.add(links);

                        Button competitionRanking = new Button(getTranslation("Competition.Ranking"), new Icon(VaadinIcon.FILE));
                        competitionRanking.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                        competitionRanking.addClickListener(event -> {
                            StreamResource streamResource = new StreamResource("competition_ranking" + competition.getId() + ".pdf",
                                () -> {
                                    byte[] pdf = competitionRankingService.getCompetitionRankingAsPdf(competition.getId());
                                    return new ByteArrayInputStream(pdf);
                                });
                            download(streamResource);
                        });
                        Div competitionRankingDiv = new Div(competitionRanking);
                        competitionRankingDiv.getClassNames().add("button-div");
                        links.add(competitionRankingDiv);

                        if (SecurityContext.isUserLoggedIn()) {
                            Button diploma = new Button(getTranslation("Diploma"), new Icon(VaadinIcon.FILE));
                            diploma.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                            diploma.addClickListener(event -> {
                                StreamResource streamResource = new StreamResource("diploma" + competition.getId() + ".pdf",
                                    () -> {
                                        byte[] pdf = competitionRankingService.getDiplomasAsPdf(competition.getId());
                                        return new ByteArrayInputStream(pdf);
                                    });
                                download(streamResource);
                            });
                            Div diplomaDiv = new Div(diploma);
                            diplomaDiv.getClassNames().add("button-div");
                            links.add(diplomaDiv);

                            Button eventRanking = new Button(getTranslation("Event.Ranking"), new Icon(VaadinIcon.FILE));
                            eventRanking.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                            eventRanking.addClickListener(event -> {
                                StreamResource streamResource = new StreamResource("event_ranking" + competition.getId() + ".pdf",
                                    () -> {
                                        byte[] pdf = competitionRankingService.getEventRankingAsPdf(competition.getId());
                                        return new ByteArrayInputStream(pdf);
                                    });
                                download(streamResource);
                            });
                            Div eventRankingDiv = new Div(eventRanking);
                            eventRankingDiv.getClassNames().add("button-div");
                            links.add(eventRankingDiv);

                            Button enterResults = new Button(getTranslation("Enter.Results"), new Icon(VaadinIcon.KEYBOARD));
                            enterResults.addThemeVariants(ButtonVariant.LUMO_ERROR);
                            enterResults.addClickListener(event -> UI.getCurrent().navigate(ResultCapturingView.class, competition.getId().toString()));
                            Div enterResultsDiv = new Div(enterResults);
                            enterResultsDiv.getClassNames().add("button-div");
                            links.add(enterResultsDiv);
                        }
                    });
            });
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
