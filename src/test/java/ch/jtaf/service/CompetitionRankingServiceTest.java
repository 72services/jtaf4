package ch.jtaf.service;

import ch.jtaf.reporting.data.CompetitionRankingData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CompetitionRankingServiceTest {

    @Autowired
    private CompetitionRankingService competitionRankingService;

    @MockBean
    private UserService userService;

    @Test
    void get_competition_ranking() {
        CompetitionRankingData competitionRanking = competitionRankingService.getCompetitionRanking(6L);

        assertThat(competitionRanking.name()).isEqualTo("39. Jugendmeisterschaft");
    }
}
