package ch.jtaf.service;

import ch.jtaf.reporting.data.CompetitionRankingData;
import ch.jtaf.reporting.data.EventsRankingData;
import ch.jtaf.reporting.report.CompetitionRankingReport;
import ch.jtaf.reporting.report.DiplomaReport;
import ch.jtaf.reporting.report.EventsRankingReport;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Event.EVENT;
import static ch.jtaf.db.tables.Result.RESULT;
import static ch.jtaf.db.tables.Series.SERIES;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectDistinct;

@Service
public class CompetitionRankingService {

    private final DSLContext dsl;

    public CompetitionRankingService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public byte[] getCompetitionRankingAsPdf(Long competitionId) {
        return new CompetitionRankingReport(getCompetitionRanking(competitionId), new Locale("de", "CH")).create();
    }

    public byte[] getDiplomasAsPdf(Long competitionId) {
        return new DiplomaReport(getCompetitionRanking(competitionId), getLogo(competitionId), new Locale("de", "CH")).create();
    }

    public byte[] getEventRankingAsPdf(Long competitionId) {
        return new EventsRankingReport(getEventsRanking(competitionId), new Locale("de", "CH")).create();
    }

    public CompetitionRankingData getCompetitionRanking(Long competitionId) {
        return dsl
            .select(
                COMPETITION.NAME,
                COMPETITION.COMPETITION_DATE,
                COMPETITION.ALWAYS_FIRST_THREE_MEDALS,
                COMPETITION.MEDAL_PERCENTAGE,
                multiset(
                    select(
                        CATEGORY.ABBREVIATION,
                        CATEGORY.NAME,
                        CATEGORY.YEAR_FROM,
                        CATEGORY.YEAR_TO,
                        multiset(
                            select(
                                ATHLETE.FIRST_NAME,
                                ATHLETE.LAST_NAME,
                                ATHLETE.YEAR_OF_BIRTH,
                                CLUB.NAME,
                                multiset(
                                    select(
                                        EVENT.ABBREVIATION,
                                        RESULT.RESULT_,
                                        RESULT.POINTS,
                                        RESULT.POSITION
                                    )
                                        .from(RESULT)
                                        .join(EVENT).on(EVENT.ID.eq(RESULT.EVENT_ID))
                                        .where(RESULT.ATHLETE_ID.eq(ATHLETE.ID))
                                        .and(RESULT.COMPETITION_ID.eq(COMPETITION.ID))
                                        .and(RESULT.CATEGORY_ID.eq(CATEGORY.ID))
                                        .orderBy(RESULT.POSITION)
                                ).convertFrom(r -> r.map(mapping(CompetitionRankingData.Category.Athlete.Result::new)))
                            )
                                .from(ATHLETE)
                                .leftOuterJoin(CLUB).on(CLUB.ID.eq(ATHLETE.CLUB_ID))
                                .join(CATEGORY_ATHLETE).on(CATEGORY_ATHLETE.CATEGORY_ID.eq(CATEGORY.ID).and(CATEGORY_ATHLETE.ATHLETE_ID.eq(ATHLETE.ID)))
                        ).convertFrom(r -> r.map(mapping(CompetitionRankingData.Category.Athlete::new)))
                    )
                        .from(CATEGORY)
                        .where(CATEGORY.SERIES_ID.eq(COMPETITION.SERIES_ID))
                        .orderBy(CATEGORY.ABBREVIATION)
                ).convertFrom(r -> r.map(mapping(CompetitionRankingData.Category::new)))
            )
            .from(COMPETITION)
            .where(COMPETITION.ID.eq(competitionId))
            .fetchOne(mapping(CompetitionRankingData::new));
    }

    public EventsRankingData getEventsRanking(Long competitionId) {
        return dsl
            .select(
                COMPETITION.NAME,
                COMPETITION.COMPETITION_DATE,
                multiset(
                    select(
                        EVENT.ABBREVIATION,
                        EVENT.GENDER,
                        EVENT.EVENT_TYPE,
                        multiset(
                            selectDistinct(ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME, ATHLETE.YEAR_OF_BIRTH,
                                CATEGORY.ABBREVIATION,
                                CLUB.NAME,
                                RESULT.RESULT_)
                                .from(ATHLETE)
                                .leftOuterJoin(CLUB).on(CLUB.ID.eq(ATHLETE.CLUB_ID))
                                .join(CATEGORY_ATHLETE).on(CATEGORY_ATHLETE.ATHLETE_ID.eq(ATHLETE.ID))
                                .join(CATEGORY).on(CATEGORY.ID.eq(CATEGORY_ATHLETE.CATEGORY_ID))
                                .join(CATEGORY_EVENT).on(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY.ID))
                                .join(RESULT).on(RESULT.ATHLETE_ID.eq(ATHLETE.ID)
                                    .and(RESULT.COMPETITION_ID.eq(COMPETITION.ID))
                                    .and(RESULT.CATEGORY_ID.eq(CATEGORY.ID))
                                    .and(RESULT.EVENT_ID.eq(EVENT.ID)))
                                .where(CATEGORY.SERIES_ID.eq(COMPETITION.SERIES_ID))
                        ).convertFrom(r -> r.map(mapping(EventsRankingData.Event.Result::new))))
                        .from(EVENT)
                        .where(EVENT.ORGANIZATION_ID.eq(SERIES.ORGANIZATION_ID))
                        .orderBy(EVENT.ABBREVIATION, EVENT.GENDER))
                    .convertFrom(r -> r.map(mapping(EventsRankingData.Event::new))))
            .from(COMPETITION)
            .join(SERIES).on(SERIES.ID.eq(COMPETITION.SERIES_ID))
            .where(COMPETITION.ID.eq(competitionId))
            .fetchOne(mapping(EventsRankingData::new));
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
}
