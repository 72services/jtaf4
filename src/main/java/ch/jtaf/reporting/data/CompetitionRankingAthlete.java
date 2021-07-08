package ch.jtaf.reporting.data;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparingInt;

public record CompetitionRankingAthlete(Long id, String firstName, String lastName, int yearOfBirth, Long clubId,
                                        List<CompetitionRankingResult> results) {

    public int totalPoints() {
        return results.stream().mapToInt(CompetitionRankingResult::points).sum();
    }

    public List<CompetitionRankingResult> sortedResults() {
        results.sort(comparingInt(CompetitionRankingResult::position));
        return results;
    }
}
