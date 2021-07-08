package ch.jtaf.service;

import ch.jtaf.reporting.data.EventsRankingData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

class CompetitionRankingServiceTest extends AbstractTestcontainersTest{

    @Autowired
    private CompetitionRankingService competitionRankingService;

    @Test
    void getEventsRanking() {
        EventsRankingData eventsRanking1 = competitionRankingService.getEventsRanking(1L);
        EventsRankingData eventsRanking2 = competitionRankingService.getEventsRanking2(1L);

        assertEquals(eventsRanking1, eventsRanking2);
    }
}
