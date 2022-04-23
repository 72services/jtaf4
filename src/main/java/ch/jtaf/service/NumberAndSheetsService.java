package ch.jtaf.service;

import ch.jtaf.reporting.data.NumbersAndSheetsAthlete;
import ch.jtaf.reporting.data.NumbersAndSheetsCompetition;
import ch.jtaf.reporting.report.NumbersReport;
import ch.jtaf.reporting.report.SheetsReport;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Series.SERIES;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multisetAgg;

@SuppressWarnings("ClassCanBeRecord")
@Service
public class NumberAndSheetsService {

    private final DSLContext dsl;

    public NumberAndSheetsService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public byte[] createNumbers(Long seriesId, Field<?>... orderBy) {
        return new NumbersReport(getAthletes(seriesId, orderBy), new Locale("de", "CH")).create();
    }

    public byte[] createSheets(Long seriesId, Long competitionId, Field<?>... orderBy) {
        return new SheetsReport(getCompetition(competitionId), getAthletes(seriesId, orderBy), getLogo(seriesId), new Locale("de", "CH")).create();
    }

    public byte[] createEmptySheets(Long seriesId, Long categoryId) {
        return new SheetsReport(createDummyAthlete(categoryId), getLogo(seriesId), new Locale("de", "CH")).create();
    }

    private NumbersAndSheetsAthlete createDummyAthlete(Long categoryId) {
        return dsl
            .select(
                DSL.inline(null, SQLDataType.BIGINT),
                DSL.inline(null, SQLDataType.VARCHAR),
                DSL.inline(null, SQLDataType.VARCHAR),
                DSL.inline(null, SQLDataType.INTEGER),
                CATEGORY.ABBREVIATION,
                DSL.inline(null, SQLDataType.VARCHAR),
                multisetAgg(
                    CATEGORY_EVENT.event().NAME,
                    CATEGORY_EVENT.event().EVENT_TYPE
                ).convertFrom(r -> r.map(mapping(NumbersAndSheetsAthlete.Event::new)))
            )
            .from(CATEGORY)
            .join(CATEGORY_EVENT).on(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY.ID))
            .where(CATEGORY.ID.eq(categoryId))
            .fetchOneInto(NumbersAndSheetsAthlete.class);
    }

    private NumbersAndSheetsCompetition getCompetition(Long competitionId) {
        return dsl
            .select(COMPETITION.NAME, COMPETITION.COMPETITION_DATE)
            .from(COMPETITION)
            .where(COMPETITION.ID.eq(competitionId))
            .fetchOneInto(NumbersAndSheetsCompetition.class);
    }

    private byte[] getLogo(Long id) {
        var logoRecord = dsl.select(SERIES.LOGO).from(SERIES).where(SERIES.ID.eq(id)).fetchOne();
        if (logoRecord != null) {
            return logoRecord.get(SERIES.LOGO);
        } else {
            return new byte[0];
        }
    }

    private List<NumbersAndSheetsAthlete> getAthletes(Long seriesId, Field<?>... orderBy) {
        Field<?>[] order = new Field[orderBy.length + 1];
        order[orderBy.length] = CATEGORY_EVENT.POSITION;
        return dsl
            .select(
                ATHLETE.ID,
                ATHLETE.FIRST_NAME,
                ATHLETE.LAST_NAME,
                ATHLETE.YEAR_OF_BIRTH,
                CATEGORY.ABBREVIATION,
                ATHLETE.club().ABBREVIATION,
                multisetAgg(
                    CATEGORY_EVENT.event().NAME,
                    CATEGORY_EVENT.event().EVENT_TYPE
                ).convertFrom(r -> r.map(mapping(NumbersAndSheetsAthlete.Event::new)))
            )
            .from(CATEGORY_ATHLETE)
            .join(ATHLETE).on(ATHLETE.ID.eq(CATEGORY_ATHLETE.ATHLETE_ID))
            .join(CATEGORY).on(CATEGORY.ID.eq(CATEGORY_ATHLETE.CATEGORY_ID))
            .join(CATEGORY_EVENT).on(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY.ID))
            .where(CATEGORY.series().ID.eq(seriesId))
            .groupBy(ATHLETE.ID,
                ATHLETE.FIRST_NAME,
                ATHLETE.LAST_NAME,
                ATHLETE.YEAR_OF_BIRTH,
                CATEGORY.ABBREVIATION,
                ATHLETE.club().ABBREVIATION)
            .orderBy(order)
            .fetch(mapping(NumbersAndSheetsAthlete::new));
    }

}

