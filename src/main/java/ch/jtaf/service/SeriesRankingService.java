package ch.jtaf.service;

import ch.jtaf.reporting.data.ClubRankingData;
import ch.jtaf.reporting.data.SeriesRankingData;
import ch.jtaf.reporting.report.ClubRankingReport;
import ch.jtaf.reporting.report.SeriesRankingReport;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Result.RESULT;
import static ch.jtaf.db.tables.Series.SERIES;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.*;

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
                COMPETITION.series().NAME,
                count(COMPETITION.ID),
                multiset(
                    select(
                        CATEGORY.ABBREVIATION,
                        CATEGORY.NAME,
                        CATEGORY.YEAR_FROM,
                        CATEGORY.YEAR_TO,
                        multiset(
                            select(
                                CATEGORY_ATHLETE.athlete().FIRST_NAME,
                                CATEGORY_ATHLETE.athlete().LAST_NAME,
                                CATEGORY_ATHLETE.athlete().YEAR_OF_BIRTH,
                                CATEGORY_ATHLETE.athlete().club().NAME,
                                multiset(
                                    select(
                                        RESULT.competition().NAME,
                                        sum(RESULT.POINTS)
                                    )
                                        .from(RESULT)
                                        .where(RESULT.ATHLETE_ID.eq(CATEGORY_ATHLETE.ATHLETE_ID))
                                        .and(RESULT.competition().SERIES_ID.eq(COMPETITION.series().ID))
                                        .groupBy(RESULT.competition().NAME, RESULT.competition().COMPETITION_DATE)
                                        .orderBy(RESULT.competition().COMPETITION_DATE)
                                ).convertFrom(r -> r.map(mapping(SeriesRankingData.Category.Athlete.Result::new)))
                            )
                                .from(CATEGORY_ATHLETE)
                                .where(CATEGORY_ATHLETE.CATEGORY_ID.eq(CATEGORY.ID))
                        ).convertFrom(r -> r.map(mapping(SeriesRankingData.Category.Athlete::new)))
                    )
                        .from(CATEGORY)
                        .where(CATEGORY.SERIES_ID.eq(COMPETITION.series().ID))
                        .orderBy(CATEGORY.ABBREVIATION)
                ).convertFrom(r -> r.map(mapping(SeriesRankingData.Category::new)))
            )
            .from(COMPETITION)
            .where(COMPETITION.SERIES_ID.eq(seriesId))
            .groupBy(COMPETITION.series().ID, COMPETITION.series().NAME)
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
                        RESULT.athlete().club().NAME,
                        sum(RESULT.POINTS)
                    )
                        .from(RESULT)
                        .where(RESULT.competition().SERIES_ID.eq(seriesId))
                        .groupBy(RESULT.athlete().club().NAME)
                ).convertFrom(r -> r.map(mapping(ClubRankingData.Result::new)))
            )
            .from(SERIES)
            .where(SERIES.ID.eq(seriesId))
            .fetchOne(mapping(ClubRankingData::new));
    }
}
