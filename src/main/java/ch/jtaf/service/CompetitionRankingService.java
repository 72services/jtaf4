package ch.jtaf.service;

import ch.jtaf.reporting.data.CompetitionRankingAthlete;
import ch.jtaf.reporting.data.CompetitionRankingCategory;
import ch.jtaf.reporting.data.CompetitionRankingData;
import ch.jtaf.reporting.data.CompetitionRankingResult;
import ch.jtaf.reporting.data.EventsRankingData;
import ch.jtaf.reporting.data.EventsRankingEvent;
import ch.jtaf.reporting.data.EventsRankingResult;
import ch.jtaf.reporting.report.CompetitionRankingReport;
import ch.jtaf.reporting.report.DiplomaReport;
import ch.jtaf.reporting.report.EventsRankingReport;
import org.jooq.DSLContext;
import org.jooq.Record14;
import org.jooq.Record9;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Event.EVENT;
import static ch.jtaf.db.tables.Result.RESULT;
import static ch.jtaf.db.tables.Series.SERIES;
import static java.util.stream.Collectors.toMap;

@Service
public class CompetitionRankingService {

    private final DSLContext dsl;

    public CompetitionRankingService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public byte[] getCompetitionRankingAsPdf(Long competitionId) {
        return new CompetitionRankingReport(getCompetitionRanking(competitionId), new Locale("de", "CH"), getClubs()).create();
    }

    public byte[] getDiplomasAsPdf(Long competitionId) {
        return new DiplomaReport(getCompetitionRanking(competitionId), getLogo(competitionId), new Locale("de", "CH")).create();
    }

    public byte[] getEventRankingAsPdf(Long competitionId) {
        return new EventsRankingReport(getEventsRanking(competitionId), new Locale("de", "CH"), getClubs()).create();
    }

    public CompetitionRankingData getCompetitionRanking(Long competitionId) {
        var competition = dsl
            .select(COMPETITION.NAME, COMPETITION.COMPETITION_DATE, COMPETITION.ALWAYS_FIRST_THREE_MEDALS, COMPETITION.MEDAL_PERCENTAGE)
            .from(COMPETITION)
            .where(COMPETITION.ID.eq(competitionId))
            .fetchOne();

        if (competition != null) {
            var competitionRanking = new CompetitionRankingData(
                competition.get(COMPETITION.NAME), competition.get(COMPETITION.COMPETITION_DATE),
                competition.get(COMPETITION.ALWAYS_FIRST_THREE_MEDALS), competition.get(COMPETITION.MEDAL_PERCENTAGE), new ArrayList<>());

            var results = dsl.
                selectDistinct(
                    CATEGORY.ID, CATEGORY.ABBREVIATION, CATEGORY.NAME, CATEGORY.YEAR_FROM, CATEGORY.YEAR_TO,
                    ATHLETE.ID, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME, ATHLETE.YEAR_OF_BIRTH,
                    ATHLETE.CLUB_ID,
                    EVENT.ABBREVIATION,
                    RESULT.RESULT_,
                    RESULT.POINTS,
                    RESULT.POSITION
                )
                .from(COMPETITION, CATEGORY, CATEGORY_ATHLETE, ATHLETE, CATEGORY_EVENT, EVENT, RESULT)
                .where(COMPETITION.ID.eq(competitionId))
                .and(CATEGORY.SERIES_ID.eq(COMPETITION.SERIES_ID))
                .and(CATEGORY_ATHLETE.ATHLETE_ID.eq(ATHLETE.ID))
                .and(CATEGORY_ATHLETE.CATEGORY_ID.eq(CATEGORY.ID))
                .and(CATEGORY.ID.eq(CATEGORY_EVENT.CATEGORY_ID))
                .and(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY.ID))
                .and(CATEGORY_EVENT.EVENT_ID.eq(EVENT.ID))
                .and(RESULT.COMPETITION_ID.eq(COMPETITION.ID))
                .and(RESULT.CATEGORY_ID.eq(CATEGORY.ID))
                .and(RESULT.ATHLETE_ID.eq(ATHLETE.ID))
                .and(EVENT.ID.eq(RESULT.EVENT_ID))
                .orderBy(CATEGORY.ABBREVIATION, ATHLETE.ID)
                .fetch();

            competitionRanking.categories().addAll(getCategories(results));

            return competitionRanking;
        } else {
            return null;
        }
    }

    public EventsRankingData getEventsRanking(Long competitionId) {
        var competition = dsl
            .select(COMPETITION.NAME, COMPETITION.COMPETITION_DATE)
            .from(COMPETITION)
            .where(COMPETITION.ID.eq(competitionId))
            .fetchOne();

        if (competition != null) {
            var eventsRanking = new EventsRankingData(competition.get(COMPETITION.NAME), competition.get(COMPETITION.COMPETITION_DATE), new ArrayList<>());

            var results = dsl.
                selectDistinct(
                    ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME, ATHLETE.YEAR_OF_BIRTH,
                    CATEGORY.ABBREVIATION,
                    ATHLETE.CLUB_ID,
                    EVENT.ABBREVIATION, EVENT.GENDER, EVENT.EVENT_TYPE,
                    RESULT.RESULT_
                )
                .from(COMPETITION, CATEGORY, CATEGORY_ATHLETE, ATHLETE, CATEGORY_EVENT, EVENT, RESULT)
                .where(COMPETITION.ID.eq(competitionId))
                .and(CATEGORY.SERIES_ID.eq(COMPETITION.SERIES_ID))
                .and(CATEGORY_ATHLETE.ATHLETE_ID.eq(ATHLETE.ID))
                .and(CATEGORY_ATHLETE.CATEGORY_ID.eq(CATEGORY.ID))
                .and(CATEGORY.ID.eq(CATEGORY_EVENT.CATEGORY_ID))
                .and(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY.ID))
                .and(CATEGORY_EVENT.EVENT_ID.eq(EVENT.ID))
                .and(RESULT.COMPETITION_ID.eq(COMPETITION.ID))
                .and(RESULT.CATEGORY_ID.eq(CATEGORY.ID))
                .and(RESULT.ATHLETE_ID.eq(ATHLETE.ID))
                .and(EVENT.ID.eq(RESULT.EVENT_ID))
                .orderBy(EVENT.ABBREVIATION, EVENT.GENDER)
                .fetch();

            eventsRanking.events().addAll(getEvents(results));

            return eventsRanking;
        } else {
            return null;
        }
    }

    private List<CompetitionRankingCategory> getCategories(Result<Record14<Long, String, String, Integer, Integer, Long, String, String, Integer, Long, String, String, Integer, Integer>> results) {
        List<CompetitionRankingCategory> categories = new ArrayList<>();

        CompetitionRankingCategory category = null;
        CompetitionRankingAthlete athlete = null;

        for (var result : results) {
            if (category == null || !category.id().equals(result.get(CATEGORY.ID))) {
                category = new CompetitionRankingCategory(result.get(CATEGORY.ID), result.get(CATEGORY.ABBREVIATION),
                    result.get(CATEGORY.NAME), result.get(CATEGORY.YEAR_FROM), result.get(CATEGORY.YEAR_TO), new ArrayList<>());
                categories.add(category);
            }

            if (athlete == null || !athlete.id().equals(result.get(ATHLETE.ID))) {
                athlete = new CompetitionRankingAthlete(result.get(ATHLETE.ID), result.get(ATHLETE.FIRST_NAME),
                    result.get(ATHLETE.LAST_NAME), result.get(ATHLETE.YEAR_OF_BIRTH), result.get(ATHLETE.CLUB_ID), new ArrayList<>());
                category.athletes().add(athlete);
            }

            CompetitionRankingResult competitionRankingResult = new CompetitionRankingResult(result.get(EVENT.ABBREVIATION),
                result.get(RESULT.RESULT_), result.get(RESULT.POINTS), result.get(RESULT.POSITION));
            athlete.results().add(competitionRankingResult);
        }

        return categories;
    }

    private byte[] getLogo(Long competitionId) {
        var logoRecord = dsl
            .select(SERIES.LOGO)
            .from(SERIES)
            .join(COMPETITION).on(COMPETITION.SERIES_ID.eq(SERIES.ID))
            .where(COMPETITION.ID.eq(competitionId))
            .fetchOne();
        if (logoRecord != null) {
            return logoRecord.get(SERIES.LOGO);
        } else {
            return new byte[0];
        }
    }

    private List<EventsRankingEvent> getEvents(Result<Record9<String, String, Integer, String, Long, String, String, String, String>> records) {
        List<EventsRankingEvent> events = new ArrayList<>();

        EventsRankingEvent event = null;

        for (var record : records) {
            if (event == null
                || (!event.abbreviation().equals(record.get(EVENT.ABBREVIATION))
                && !event.abbreviation().equals(record.get(EVENT.GENDER)))) {
                event = new EventsRankingEvent(record.get(EVENT.ABBREVIATION), record.get(EVENT.GENDER), new ArrayList<>());
                events.add(event);
            }
            EventsRankingResult result = new EventsRankingResult(record.get(ATHLETE.FIRST_NAME), record.get(ATHLETE.LAST_NAME),
                record.get(ATHLETE.YEAR_OF_BIRTH), record.get(CATEGORY.ABBREVIATION), record.get(ATHLETE.CLUB_ID),
                record.get(EVENT.ABBREVIATION), record.get(EVENT.EVENT_TYPE), record.get(RESULT.RESULT_));
            event.sortedResults().add(result);
        }

        return events;
    }

    private Map<Long, String> getClubs() {
        return dsl.
            select(CLUB.ID, CLUB.ABBREVIATION)
            .from(CLUB)
            .stream()
            .collect(toMap(club -> club.get(CLUB.ID), club -> club.get(CLUB.ABBREVIATION)));
    }
}
