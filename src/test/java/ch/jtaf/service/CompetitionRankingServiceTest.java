package ch.jtaf.service;

import ch.jtaf.reporting.data.EventsRankingData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CompetitionRankingServiceTest {

    @Autowired
    private CompetitionRankingService competitionRankingService;

    @Test
    void getEventsRanking() {
        EventsRankingData eventsRanking1 = competitionRankingService.getEventsRanking(1L);
        EventsRankingData eventsRanking2 = competitionRankingService.getEventsRanking2(1L);

        assertEquals(eventsRanking1, eventsRanking2);
    }
}
