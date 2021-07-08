package ch.jtaf.reporting.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public record SeriesRankingAthlete(Long id, String firstName, String lastName, int yearOfBirth, Long clubId,
                                   List<SeriesRankingResult> results) {

    public int totalPoints() {
        return results.stream().map(SeriesRankingResult::points).mapToInt(BigDecimal::intValue).sum();
    }

    public List<SeriesRankingResult> sortedResults() {
        results.sort(comparing(SeriesRankingResult::competitionDate));
        return results;
    }
}
