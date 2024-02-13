package ch.jtaf.service;

import ch.jtaf.reporting.data.ClubRankingData;
import ch.jtaf.reporting.data.SeriesRankingData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SeriesRankingServiceTest {

    @Autowired
    private SeriesRankingService seriesRankingService;

    @Test
    void get_club_ranking() {
        ClubRankingData clubRanking = seriesRankingService.getClubRanking(1L);

        assertThat(clubRanking.sortedResults()).hasSize(4);
    }

    @Test
    void create_club_ranking_pdf() {
        byte[] pdf = seriesRankingService.getClubRankingAsPdf(1L);

        assertThat(pdf).isNotEmpty();
    }

    @Test
    void get_series_ranking() {
        SeriesRankingData seriesRanking = seriesRankingService.getSeriesRanking(3L);

        assertThat(seriesRanking.name()).isEqualTo("CIS 2019");
    }

    @Test
    void create_series_ranking_pdf() {
        byte[] pdf = seriesRankingService.getSeriesRankingAsPdf(3L);

        assertThat(pdf).isNotEmpty();
    }
}
