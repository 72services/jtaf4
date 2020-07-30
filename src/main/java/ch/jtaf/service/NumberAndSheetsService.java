package ch.jtaf.service;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Event.EVENT;
import static ch.jtaf.db.tables.Series.SERIES;
import static org.jooq.impl.DSL.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Service;

import ch.jtaf.reporting.data.NumbersAndSheetsAthlete;
import ch.jtaf.reporting.data.NumbersAndSheetsCompetition;
import ch.jtaf.reporting.data.NumbersAndSheetsEvent;
import ch.jtaf.reporting.report.NumbersReport;
import ch.jtaf.reporting.report.SheetsReport;

@Service
public class NumberAndSheetsService {

    private final DSLContext dsl;

    public NumberAndSheetsService(DSLContext dsl) {
        this.dsl = dsl;
    }

    public byte[] createNumbers(Long competitionId, String order) {
        return new NumbersReport(getAthletes(competitionId, order), new Locale("de", "CH")).create();
    }

    public byte[] createSheets(Long competitionId, String order) {
        return new SheetsReport(getCompetition(competitionId), getAthletes(competitionId, order), getLogo(competitionId), new Locale("de", "CH")).create();
    }

    public byte[] createEmptySheets(Long competitionId, Long categoryId) {
        return new SheetsReport(createDummyAthlete(categoryId), getLogo(competitionId), new Locale("de", "CH")).create();
    }

    private NumbersAndSheetsAthlete createDummyAthlete(Long categoryId) {
        var records = dsl
                .select(CATEGORY.ABBREVIATION,
                        EVENT.ABBREVIATION, EVENT.NAME, EVENT.EVENT_TYPE, EVENT.EVENT_TYPE, EVENT.GENDER,
                        CATEGORY_EVENT.POSITION)
                .from(CATEGORY)
                .join(CATEGORY_EVENT).on(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY.ID))
                .join(EVENT).on(EVENT.ID.eq(CATEGORY_EVENT.EVENT_ID))
                .where(CATEGORY.ID.eq(categoryId))
                .orderBy(CATEGORY_EVENT.POSITION)
                .fetch();

        var athlete = new NumbersAndSheetsAthlete(null, null, null, 0, records.get(0).get(CATEGORY.ABBREVIATION), null);

        for (var record : records) {
            athlete.getEvents().add(new NumbersAndSheetsEvent(record.get(EVENT.ABBREVIATION), record.get(EVENT.NAME),
                    record.get(EVENT.EVENT_TYPE), record.get(EVENT.GENDER), record.get(CATEGORY_EVENT.POSITION)));
        }

        return athlete;
    }

    private NumbersAndSheetsCompetition getCompetition(Long competitionId) {
        return dsl
                .select(COMPETITION.ID, COMPETITION.NAME, COMPETITION.COMPETITION_DATE)
                .from(COMPETITION)
                .where(COMPETITION.ID.eq(competitionId))
                .fetchOneInto(NumbersAndSheetsCompetition.class);
    }

    private byte[] getLogo(Long id) {
        var logoRecord = dsl
                .select(SERIES.LOGO)
                .from(SERIES)
                .join(COMPETITION).on(COMPETITION.SERIES_ID.eq(SERIES.ID))
                .where(COMPETITION.ID.eq(id))
                .fetchOne();
        return logoRecord.get(SERIES.LOGO);
    }

    private List<NumbersAndSheetsAthlete> getAthletes(Long competitionId, String order) {
        var athletes = new ArrayList<NumbersAndSheetsAthlete>();

        var records = dsl
                .select(ATHLETE.ID, ATHLETE.FIRST_NAME, ATHLETE.LAST_NAME, ATHLETE.YEAR_OF_BIRTH,
                        CATEGORY.ABBREVIATION,
                        CLUB.ABBREVIATION,
                        EVENT.ABBREVIATION, EVENT.NAME, EVENT.GENDER, EVENT.EVENT_TYPE,
                        CATEGORY_EVENT.POSITION)
                .from(ATHLETE)
                .join(CATEGORY_ATHLETE).on(CATEGORY_ATHLETE.ATHLETE_ID.eq(ATHLETE.ID))
                .join(CATEGORY).on(CATEGORY.ID.eq(CATEGORY_ATHLETE.CATEGORY_ID))
                .join(COMPETITION).on(COMPETITION.SERIES_ID.eq(CATEGORY.SERIES_ID))
                .leftOuterJoin(CLUB).on(CLUB.ID.eq(ATHLETE.CLUB_ID))
                .join(CATEGORY_EVENT).on(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY.ID))
                .join(EVENT).on(EVENT.ID.eq(CATEGORY_EVENT.EVENT_ID))
                .where(COMPETITION.ID.eq(competitionId))
                .orderBy(field(order))
                .fetch();

        NumbersAndSheetsAthlete athlete = null;

        for (Record record : records) {
            if (athlete == null || !athlete.getId().equals(record.get(ATHLETE.ID))) {
                athlete = new NumbersAndSheetsAthlete(record.get(ATHLETE.ID), record.get(ATHLETE.FIRST_NAME), record.get(ATHLETE.LAST_NAME),
                        record.get(ATHLETE.YEAR_OF_BIRTH), record.get(CATEGORY.ABBREVIATION), record.get(CLUB.ABBREVIATION));
                athletes.add(athlete);
            }
            athlete.getEvents().add(new NumbersAndSheetsEvent(record.get(EVENT.ABBREVIATION), record.get(EVENT.NAME),
                    record.get(EVENT.GENDER), record.get(EVENT.EVENT_TYPE), record.get(CATEGORY_EVENT.POSITION)));
        }

        return athletes;
    }

}

