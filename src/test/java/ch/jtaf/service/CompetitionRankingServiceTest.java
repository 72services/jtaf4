package ch.jtaf.service;

import ch.jtaf.reporting.data.CompetitionRankingData;
import ch.jtaf.reporting.data.EventsRankingData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CompetitionRankingServiceTest {

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private CompetitionRankingService competitionRankingService;

    @Test
    void get_competition_ranking() {
        CompetitionRankingData competitionRanking = competitionRankingService.getCompetitionRanking(6L);

        assertThat(competitionRanking.name()).isEqualTo("39. Jugendmeisterschaft");
    }

    @Test
    void create_competition_ranking_pdf() {
        byte[] pdf = competitionRankingService.getCompetitionRankingAsPdf(6L);

        assertThat(pdf).isNotEmpty();
    }

    @Test
    void get_events_ranking() {
        EventsRankingData eventsRanking = competitionRankingService.getEventsRanking(6L);

        assertThat(eventsRanking.name()).isEqualTo("39. Jugendmeisterschaft");
    }

    @Test
    void get_events_ranking_pdf() {
        byte[] pdf = competitionRankingService.getEventRankingAsPdf(6L);

        assertThat(pdf).isNotEmpty();
    }

    @Test
    void get_diplomas_pdf() {
        byte[] pdf = competitionRankingService.getDiplomasAsPdf(6L);

        assertThat(pdf).isNotEmpty();
    }
}
