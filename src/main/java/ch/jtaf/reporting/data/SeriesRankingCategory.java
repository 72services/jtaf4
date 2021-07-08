package ch.jtaf.reporting.data;

import java.util.List;

import static java.lang.Integer.compare;
import static java.util.stream.Collectors.toList;

public record  SeriesRankingCategory(Long id, String abbreviation, String name, int yearFrom, int yearTo, int numberOfCompetitions,
                                     List<SeriesRankingAthlete> athletes) {

    public List<SeriesRankingAthlete> getFilteredAthletes() {
        return athletes.stream()
            .filter(seriesRankingAthlete -> seriesRankingAthlete.sortedResults().size() == numberOfCompetitions)
            .sorted((o1, o2) -> compare(o2.totalPoints(), o1.totalPoints()))
            .collect(toList());
    }
}
