package ch.jtaf.service;

import ch.jtaf.reporting.data.CompetitionRankingData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CompetitionRankingServiceTest extends AbstractTestcontainersTest {

    @Autowired
    private CompetitionRankingService competitionRankingService;

    @Test
    void getCompetitionRanking() {
        CompetitionRankingData competitionRanking = competitionRankingService.getCompetitionRanking(6L);
        System.out.println(competitionRanking);
    }
}
