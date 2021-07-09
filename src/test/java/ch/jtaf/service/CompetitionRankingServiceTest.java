package ch.jtaf.service;

import ch.jtaf.reporting.data.EventsRankingData;
import org.jooq.DSLContext;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompetitionRankingServiceTest extends AbstractTestcontainersTest {

    @Autowired
    private CompetitionRankingService competitionRankingService;

    @Test
    @Ignore
    void getEventsRanking() {
        EventsRankingData eventsRanking1 = competitionRankingService.getEventsRanking(1L);
        EventsRankingData eventsRanking2 = competitionRankingService.getEventsRanking2(1L);

        assertEquals(eventsRanking1, eventsRanking2);
    }
}
