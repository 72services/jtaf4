package ch.jtaf.reporting.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.lang.Integer.compare;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public record SeriesRankingData(String name, int numberOfCompetitions, List<SeriesRankingCategory> categories) {

    public static record SeriesRankingCategory(Long id, String abbreviation, String name, int yearFrom, int yearTo,
                                               List<SeriesRankingAthlete> athletes) {

        public List<SeriesRankingAthlete> getFilteredAthletes(int numberOfCompetitions) {
            return athletes.stream()
                .filter(seriesRankingAthlete -> seriesRankingAthlete.sortedResults().size() == numberOfCompetitions)
                .sorted((o1, o2) -> compare(o2.totalPoints(), o1.totalPoints()))
                .collect(toList());
        }

        public static record SeriesRankingAthlete(Long id, String firstName, String lastName, int yearOfBirth, Long clubId,
                                                  List<SeriesRankingResult> results) {

            public int totalPoints() {
                return results.stream().map(SeriesRankingResult::points).mapToInt(BigDecimal::intValue).sum();
            }

            public List<SeriesRankingResult> sortedResults() {
                results.sort(comparing(SeriesRankingResult::competitionDate));
                return results;
            }

            public static record SeriesRankingResult(Long athleteId, Long competitionId, String competitionName,
                                                     LocalDate competitionDate,
                                                     BigDecimal points) {
            }

        }

    }

}
