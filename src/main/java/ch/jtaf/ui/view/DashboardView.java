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
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.ByteArrayInputStream;
import java.io.Serial;
import java.time.format.DateTimeFormatter;

import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Series.SERIES;
import static ch.jtaf.ui.util.LogoUtil.resizeLogo;

@AnonymousAllowed
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements HasDynamicTitle {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String NAME_MIN_WIDTH = "350px";
    private static final String BUTTON_WIDTH = "220px";

    public DashboardView(DSLContext dsl, SeriesRankingService seriesRankingService, CompetitionRankingService competitionRankingService) {
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
            pSeriesName.setMinWidth(NAME_MIN_WIDTH);
            seriesLayout.add(pSeriesName);

            var buttonLayout = new HorizontalLayout();
            buttonLayout.getClassNames().add("button-layout");
            seriesLayout.add(buttonLayout);

            var seriesRankingAnchor = new Anchor(new StreamResource("series_ranking" + series.getId() + ".pdf",
                () -> {
                    var pdf = seriesRankingService.getSeriesRankingAsPdf(series.getId());
                    return new ByteArrayInputStream(pdf);
                }), "");
            seriesRankingAnchor.setId("series-ranking-" + seriesIndex);
            seriesRankingAnchor.setTarget("_blank");

            Button seriesRankingButton = new Button(getTranslation("Series.Ranking"), new Icon(VaadinIcon.FILE));
            seriesRankingButton.setWidth(BUTTON_WIDTH);
            seriesRankingButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            seriesRankingButton.addClassName(LumoUtility.FontWeight.MEDIUM);
            seriesRankingAnchor.add(seriesRankingButton);

            var seriesRankingDiv = new Div(seriesRankingAnchor);
            buttonLayout.add(seriesRankingDiv);

            var clubRankingAnchor = new Anchor(new StreamResource("club_ranking" + series.getId() + ".pdf",
                () -> {
                    byte[] pdf = seriesRankingService.getClubRankingAsPdf(series.getId());
                    return new ByteArrayInputStream(pdf);
                }), "");
            clubRankingAnchor.setId("club-ranking-" + seriesIndex);
            clubRankingAnchor.setTarget("_blank");

            Button clubRankingButton = new Button(getTranslation("Club.Ranking"), new Icon(VaadinIcon.FILE));
            clubRankingButton.setWidth(BUTTON_WIDTH);
            clubRankingButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            clubRankingButton.addClassName(LumoUtility.FontWeight.MEDIUM);
            clubRankingAnchor.add(clubRankingButton);

            var clubRankingDiv = new Div(clubRankingAnchor);
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

                var dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                var pCompetition = new Paragraph("%s %s".formatted(competition.getName(), dateTimeFormatter.format(competition.getCompetitionDate())));
                pCompetition.setMinWidth(NAME_MIN_WIDTH);
                competitionLayout.add(pCompetition);

                var links = new HorizontalLayout();
                links.getClassNames().add("links-layout");
                competitionLayout.add(links);

                var competitionRankingAnchor = new Anchor(new StreamResource("competition_ranking" + competition.getId() + ".pdf",
                    () -> {
                        byte[] pdf = competitionRankingService.getCompetitionRankingAsPdf(competition.getId());
                        return new ByteArrayInputStream(pdf);
                    }), "");
                competitionRankingAnchor.setId("competition-ranking-" + seriesIndex + "-" + competitionIndex);
                competitionRankingAnchor.setTarget("_blank");

                Button competitionRankingButton = new Button(getTranslation("Competition.Ranking"), new Icon(VaadinIcon.FILE));
                competitionRankingButton.setWidth(BUTTON_WIDTH);
                competitionRankingButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                competitionRankingButton.addClassName(LumoUtility.FontWeight.MEDIUM);
                competitionRankingAnchor.add(competitionRankingButton);

                var competitionRankingDiv = new Div(competitionRankingAnchor);
                links.add(competitionRankingDiv);

                if (SecurityContext.isUserLoggedIn()) {
                    var diplomaAnchor = new Anchor(new StreamResource("diploma" + competition.getId() + ".pdf",
                        () -> {
                            var pdf = competitionRankingService.getDiplomasAsPdf(competition.getId());
                            return new ByteArrayInputStream(pdf);
                        }), "");
                    diplomaAnchor.setId("diploma-" + seriesIndex + "-" + competitionIndex);
                    diplomaAnchor.setTarget("_blank");

                    Button diplomaButton = new Button(getTranslation("Diploma"), new Icon(VaadinIcon.FILE));
                    diplomaButton.setWidth(BUTTON_WIDTH);
                    diplomaButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                    diplomaButton.addClassName(LumoUtility.FontWeight.MEDIUM);
                    diplomaAnchor.add(diplomaButton);

                    var diplomaDiv = new Div(diplomaAnchor);
                    links.add(diplomaDiv);

                    var eventRankingAnchor = new Anchor(new StreamResource("event_ranking" + competition.getId() + ".pdf",
                        () -> {
                            var pdf = competitionRankingService.getEventRankingAsPdf(competition.getId());
                            return new ByteArrayInputStream(pdf);
                        }), "");
                    eventRankingAnchor.setId("event-ranking-" + seriesIndex + "-" + competitionIndex);
                    eventRankingAnchor.setTarget("_blank");

                    Button eventRankingButton = new Button(getTranslation("Event.Ranking"), new Icon(VaadinIcon.FILE));
                    eventRankingButton.setWidth(BUTTON_WIDTH);
                    eventRankingButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                    eventRankingButton.addClassName(LumoUtility.FontWeight.MEDIUM);
                    eventRankingAnchor.add(eventRankingButton);

                    var eventRankingDiv = new Div(eventRankingAnchor);
                    links.add(eventRankingDiv);

                    var enterResults = new Button(getTranslation("Enter.Results"), new Icon(VaadinIcon.KEYBOARD));
                    enterResults.setId("enter-results-" + seriesIndex + "-" + competitionIndex);
                    enterResults.addThemeVariants(ButtonVariant.LUMO_ERROR);
                    enterResults.setWidth(BUTTON_WIDTH);
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
}
