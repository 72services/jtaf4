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

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Series.SERIES;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;

@Service
public class NumberAndSheetsService {

    private final DSLContext dsl;

    public NumberAndSheetsService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public byte[] createNumbers(Long competitionId, Field<?>... orderBy) {
        return new NumbersReport(getAthletes(competitionId, orderBy), new Locale("de", "CH")).create();
    }

    public byte[] createSheets(Long competitionId, Field<?>... orderBy) {
        return new SheetsReport(getCompetition(competitionId), getAthletes(competitionId, orderBy), getLogo(competitionId), new Locale("de", "CH")).create();
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
                multiset(
                    select(
                        CATEGORY_EVENT.event().ABBREVIATION,
                        CATEGORY_EVENT.event().NAME,
                        CATEGORY_EVENT.event().GENDER,
                        CATEGORY_EVENT.event().EVENT_TYPE,
                        CATEGORY_EVENT.POSITION
                    )
                        .from(CATEGORY_EVENT)
                        .where(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY.ID))
                        .orderBy(CATEGORY_EVENT.POSITION)
                ).convertFrom(r -> r.map(mapping(NumbersAndSheetsAthlete.Event::new)))
            )
            .from(CATEGORY)
            .where(CATEGORY.ID.eq(categoryId))
            .fetchOne(mapping(NumbersAndSheetsAthlete::new));
    }

    private NumbersAndSheetsCompetition getCompetition(Long competitionId) {
        return dsl
            .select(COMPETITION.ID, COMPETITION.NAME, COMPETITION.COMPETITION_DATE)
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

    private List<NumbersAndSheetsAthlete> getAthletes(Long competitionId, Field<?>... orderBy) {
        return dsl
            .select(
                CATEGORY_ATHLETE.athlete().ID,
                CATEGORY_ATHLETE.athlete().FIRST_NAME,
                CATEGORY_ATHLETE.athlete().LAST_NAME,
                CATEGORY_ATHLETE.athlete().YEAR_OF_BIRTH,
                CATEGORY_ATHLETE.category().ABBREVIATION,
                CATEGORY_ATHLETE.athlete().club().ABBREVIATION,
                multiset(
                    select(
                        CATEGORY_EVENT.event().ABBREVIATION,
                        CATEGORY_EVENT.event().NAME,
                        CATEGORY_EVENT.event().GENDER,
                        CATEGORY_EVENT.event().EVENT_TYPE,
                        CATEGORY_EVENT.POSITION
                    )
                        .from(CATEGORY_EVENT)
                        .where(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY_ATHLETE.CATEGORY_ID))
                        .orderBy(CATEGORY_EVENT.POSITION)
                ).convertFrom(r -> r.map(mapping(NumbersAndSheetsAthlete.Event::new)))
            )
            .from(CATEGORY_ATHLETE)
            .where(COMPETITION.ID.eq(competitionId))
            .orderBy(orderBy)
            .fetch(mapping(NumbersAndSheetsAthlete::new));
    }

}

