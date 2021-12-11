package ch.jtaf.service;

import ch.jtaf.reporting.data.CompetitionRankingData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CompetitionRankingServiceTest {

    @Autowired
    private CompetitionRankingService competitionRankingService;

    @Test
    void getCompetitionRanking() {
        CompetitionRankingData competitionRanking = competitionRankingService.getCompetitionRanking(6L);

        assertThat(competitionRanking.name()).isEqualTo("39. Jugendmeisterschaft");
    }
}
