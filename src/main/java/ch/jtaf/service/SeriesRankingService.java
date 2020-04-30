package ch.jtaf.service;

import ch.jtaf.reporting.data.ClubRankingData;
import ch.jtaf.reporting.data.ClubResultData;
import ch.jtaf.reporting.data.SeriesRankingAthlete;
import ch.jtaf.reporting.data.SeriesRankingCategory;
import ch.jtaf.reporting.data.SeriesRankingData;
import ch.jtaf.reporting.data.SeriesRankingResult;
import ch.jtaf.reporting.report.ClubRankingReport;
import ch.jtaf.reporting.report.SeriesRankingReport;
import org.jooq.DSLContext;
import org.jooq.Record14;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.sum;

@Service
public class SeriesRankingService {

    private final DSLContext dsl;

    public SeriesRankingService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public byte[] getSeriesRankingAsPdf(Long seriesId) {
        return new SeriesRankingReport(getSeriesRanking(seriesId), new Locale("de", "CH"), getClubs()).create();
    }

    public SeriesRankingData getSeriesRanking(Long seriesId) {
        var series = dsl
                .select(SERIES.ID, SERIES.NAME, count(COMPETITION.ID))
                .from(SERIES)
                .join(COMPETITION).on(COMPETITION.SERIES_ID.eq(SERIES.ID))
                .where(SERIES.ID.eq(seriesId))
                .groupBy(SERIES.ID, SERIES.NAME)
                .fetchOne();

        var seriesRanking = new SeriesRankingData(series.get(SERIES.NAME), series.get(count(COMPETITION.ID)));

        var results = dsl.
                select(
                        CATEGORY.ID, CATEGORY.ABBREVIATION, CATEGORY.NAME, CATEGORY.YEAR_FROM, CATEGORY.YEAR_TO,
                        ATHLETE.ID, ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME, ATHLETE.YEAR_OF_BIRTH,
                        ATHLETE.CLUB_ID,
                        COMPETITION.ID, COMPETITION.NAME, COMPETITION.COMPETITION_DATE,
                        sum(RESULT.POINTS)
                )
                .from(COMPETITION, CATEGORY, CATEGORY_ATHLETE, ATHLETE, CATEGORY_EVENT, EVENT, RESULT)
                .where(COMPETITION.SERIES_ID.eq(seriesId))
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
                .groupBy(CATEGORY.ID, ATHLETE.ID, COMPETITION.ID)
                .having(sum(RESULT.POINTS).gt(ZERO))
                .orderBy(CATEGORY.ID, ATHLETE.ID, COMPETITION.ID)
                .fetch();

        seriesRanking.getCategories().addAll(getCategories(seriesRanking, results));

        return seriesRanking;
    }

    public byte[] getClubRankingAsPdf(Long seriesId) {
        return new ClubRankingReport(getClubRanking(seriesId), new Locale("de", "CH"), getClubs()).create();
    }

    public ClubRankingData getClubRanking(Long seriesId) {
        var series = dsl
                .select(SERIES.NAME)
                .from(SERIES)
                .where(SERIES.ID.eq(seriesId))
                .fetchOne();

        var clubRanking = new ClubRankingData(series.get(SERIES.NAME));

        var results = dsl.
                select(CLUB.NAME, sum(RESULT.POINTS))
                .from(RESULT, COMPETITION, EVENT, CATEGORY_EVENT, CATEGORY, ATHLETE)
                .leftOuterJoin(CLUB).on(CLUB.ID.eq(ATHLETE.CLUB_ID))
                .where(COMPETITION.SERIES_ID.eq(seriesId))
                .and(COMPETITION.ID.eq(RESULT.COMPETITION_ID))
                .and(EVENT.ID.eq(RESULT.EVENT_ID))
                .and(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY.ID)).and(CATEGORY_EVENT.EVENT_ID.eq(EVENT.ID))
                .and(CATEGORY.ID.eq(CATEGORY_EVENT.CATEGORY_ID))
                .and(ATHLETE.ID.eq(RESULT.ATHLETE_ID))
                .groupBy(CLUB.NAME)
                .fetch();

        clubRanking.getResults().addAll(getResults(results));

        return clubRanking;
    }

    private List<SeriesRankingCategory> getCategories(SeriesRankingData seriesRankingData, Result<Record14<Long, String, String, Integer, Integer, Long, String, String, Integer, Long, Long, String, LocalDate, BigDecimal>> records) {
        List<SeriesRankingCategory> categories = new ArrayList<>();

        SeriesRankingCategory category = null;
        SeriesRankingAthlete athlete = null;
        SeriesRankingResult result = null;

        for (var record : records) {
            if (category == null || !category.getId().equals(record.get(CATEGORY.ID))) {
                category = new SeriesRankingCategory(record.get(CATEGORY.ID), record.get(CATEGORY.ABBREVIATION),
                        record.get(CATEGORY.NAME), record.get(CATEGORY.YEAR_FROM), record.get(CATEGORY.YEAR_TO),
                        seriesRankingData.getNumberOfCompetitions());
                categories.add(category);
            }

            if (athlete == null || !athlete.getId().equals(record.get(ATHLETE.ID))) {
                athlete = new SeriesRankingAthlete(record.get(ATHLETE.ID), record.get(ATHLETE.FIRST_NAME),
                        record.get(ATHLETE.LAST_NAME), record.get(ATHLETE.YEAR_OF_BIRTH), record.get(ATHLETE.CLUB_ID));
                category.addAthlete(athlete);
            }

            if (result != null && result.getAthleteId().equals(record.get(ATHLETE.ID)) && result.getCompetionId().equals(record.get(COMPETITION.ID))) {
                result.addPoints(record.get(sum(RESULT.POINTS)));
            } else {
                result = new SeriesRankingResult(record.get(ATHLETE.ID), record.get(COMPETITION.ID),
                        record.get(COMPETITION.NAME), record.get(COMPETITION.COMPETITION_DATE), record.get(sum(RESULT.POINTS)));
                athlete.addResult(result);
            }
        }
        return categories;
    }

    private List<ClubResultData> getResults(Result<Record2<String, BigDecimal>> records) {
        return records.stream()
                .map(record -> new ClubResultData(record.get(CLUB.NAME), record.get(sum(RESULT.POINTS))))
                .collect(toList());
    }

    private Map<Long, String> getClubs() {
        return dsl.
                select(CLUB.ID, CLUB.ABBREVIATION)
                .from(CLUB)
                .stream()
                .collect(toMap(club -> club.get(CLUB.ID), club -> club.get(CLUB.ABBREVIATION)));
    }
}
