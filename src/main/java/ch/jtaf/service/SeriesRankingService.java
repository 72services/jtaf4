package ch.jtaf.service;

import ch.jtaf.reporting.data.ClubRankingData;
import ch.jtaf.reporting.data.SeriesRankingData;
import ch.jtaf.reporting.report.ClubRankingReport;
import ch.jtaf.reporting.report.SeriesRankingReport;
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
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.sum;

@Service
public class SeriesRankingService {

    private final DSLContext dsl;

    public SeriesRankingService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public byte[] getSeriesRankingAsPdf(Long seriesId) {
        return new SeriesRankingReport(getSeriesRanking(seriesId), new Locale("de", "CH")).create();
    }

    public SeriesRankingData getSeriesRanking(Long seriesId) {
        return dsl
            .select(
                SERIES.NAME,
                count(COMPETITION.ID),
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
                                        COMPETITION.NAME,
                                        sum(RESULT.POINTS)
                                    )
                                        .from(RESULT)
                                        .join(COMPETITION).on(COMPETITION.ID.eq(RESULT.COMPETITION_ID))
                                        .where(RESULT.ATHLETE_ID.eq(ATHLETE.ID))
                                        .and(COMPETITION.SERIES_ID.eq(SERIES.ID))
                                        .groupBy(RESULT.ATHLETE_ID, RESULT.COMPETITION_ID, COMPETITION.NAME, COMPETITION.COMPETITION_DATE)
                                        .orderBy(COMPETITION.COMPETITION_DATE)
                                ).convertFrom(r -> r.map(mapping(SeriesRankingData.Category.Athlete.Result::new)))
                            )
                                .from(ATHLETE)
                                .leftOuterJoin(CLUB).on(CLUB.ID.eq(ATHLETE.CLUB_ID))
                                .join(CATEGORY_ATHLETE).on(CATEGORY_ATHLETE.ATHLETE_ID.eq(ATHLETE.ID)).and(CATEGORY_ATHLETE.CATEGORY_ID.eq(CATEGORY.ID))
                        ).convertFrom(r -> r.map(mapping(SeriesRankingData.Category.Athlete::new)))
                    )
                        .from(CATEGORY)
                        .where(CATEGORY.SERIES_ID.eq(SERIES.ID))
                        .orderBy(CATEGORY.ABBREVIATION)
                ).convertFrom(r -> r.map(mapping(SeriesRankingData.Category::new)))
            )
            .from(SERIES)
            .join(COMPETITION).on(COMPETITION.SERIES_ID.eq(SERIES.ID))
            .where(SERIES.ID.eq(seriesId))
            .groupBy(SERIES.ID, SERIES.NAME)
            .fetchOne(mapping(SeriesRankingData::new));
    }

    public byte[] getClubRankingAsPdf(Long seriesId) {
        return new ClubRankingReport(getClubRanking(seriesId), new Locale("de", "CH")).create();
    }

    public ClubRankingData getClubRanking(Long seriesId) {
        return dsl
            .select(
                SERIES.NAME,
                multiset(
                    select(
                        CLUB.NAME,
                        sum(RESULT.POINTS)
                    )
                        .from(RESULT)
                        .join(COMPETITION).on(COMPETITION.ID.eq(RESULT.COMPETITION_ID))
                        .join(EVENT).on(EVENT.ID.eq(RESULT.EVENT_ID))
                        .join(CATEGORY_EVENT).on(CATEGORY_EVENT.EVENT_ID.eq(EVENT.ID))
                        .join(ATHLETE).on(RESULT.ATHLETE_ID.eq(ATHLETE.ID))
                        .join(CLUB).on(CLUB.ID.eq(ATHLETE.CLUB_ID))
                        .where(COMPETITION.SERIES_ID.eq(seriesId))
                        .groupBy(CLUB.NAME)
                ).convertFrom(r -> r.map(mapping(ClubRankingData.Result::new)))
            )
            .from(SERIES)
            .where(SERIES.ID.eq(seriesId))
            .fetchOne(mapping(ClubRankingData::new));
    }
}
