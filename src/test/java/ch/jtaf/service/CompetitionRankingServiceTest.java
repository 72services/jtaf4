package ch.jtaf.service;

import ch.jtaf.reporting.data.EventsRankingData;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Event.EVENT;
import static ch.jtaf.db.tables.Result.RESULT;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;
import static org.junit.jupiter.api.Assertions.*;

class CompetitionRankingServiceTest extends AbstractTestcontainersTest {

    @Autowired
    private CompetitionRankingService competitionRankingService;
    @Autowired
    private DSLContext dsl;

    @Test
    void getEventsRanking() {
        EventsRankingData eventsRanking1 = competitionRankingService.getEventsRanking(1L);
        EventsRankingData eventsRanking2 = competitionRankingService.getEventsRanking2(1L);

        assertEquals(eventsRanking1, eventsRanking2);
    }

    @Test
    void query() {
        dsl
            .select(
                COMPETITION.NAME,
                COMPETITION.COMPETITION_DATE,
                multiset(
                    select(
                        EVENT.ABBREVIATION,
                        EVENT.GENDER,
                        multiset(
                            select(ATHLETE.LAST_NAME, ATHLETE.FIRST_NAME, ATHLETE.YEAR_OF_BIRTH,
                                CATEGORY.ABBREVIATION,
                                ATHLETE.CLUB_ID,
                                EVENT.ABBREVIATION, EVENT.EVENT_TYPE,
                                RESULT.RESULT_)
                                .from(COMPETITION, CATEGORY, CATEGORY_ATHLETE, ATHLETE, CATEGORY_EVENT, EVENT, RESULT)
                                .where(CATEGORY.SERIES_ID.eq(COMPETITION.SERIES_ID))
                                .and(CATEGORY_ATHLETE.ATHLETE_ID.eq(ATHLETE.ID))
                                .and(CATEGORY_ATHLETE.CATEGORY_ID.eq(CATEGORY.ID))
                                .and(CATEGORY.ID.eq(CATEGORY_EVENT.CATEGORY_ID))
                                .and(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY.ID))
                                .and(CATEGORY_EVENT.EVENT_ID.eq(EVENT.ID))
                                .and(RESULT.COMPETITION_ID.eq(COMPETITION.ID))
                                .and(RESULT.CATEGORY_ID.eq(CATEGORY.ID))
                                .and(RESULT.ATHLETE_ID.eq(ATHLETE.ID))
                                .and(EVENT.ID.eq(RESULT.EVENT_ID)))
                            .convertFrom(r -> r.map(mapping(EventsRankingData.EventsRankingEvent.Result::new))))
                        .from(EVENT)
                        .orderBy(EVENT.ABBREVIATION, EVENT.GENDER))
                    .convertFrom(r -> r.map(mapping(EventsRankingData.EventsRankingEvent::new))))
            .from(COMPETITION)
            .where(COMPETITION.ID.eq(1L))
            .fetchOne(mapping(EventsRankingData::new));
    }
}
