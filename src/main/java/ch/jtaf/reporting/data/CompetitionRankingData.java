package ch.jtaf.reporting.data;

import java.time.LocalDate;
import java.util.List;

import static java.util.Comparator.comparingInt;

public record CompetitionRankingData(String name, LocalDate competitionDate, boolean alwaysFirstThreeMedals, int medalPercentage,
                                     List<CompetitionRankingCategory> categories) {

    public static record CompetitionRankingCategory(Long id, String abbreviation, String name, int yearFrom, int yearTo,
                                                    List<CompetitionRankingAthlete> athletes) {

        public List<CompetitionRankingAthlete> sortedAthletes() {
            athletes.sort((o1, o2) -> Integer.compare(o2.totalPoints(), o1.totalPoints()));
            return athletes;
        }

        public static record CompetitionRankingAthlete(Long id, String firstName, String lastName, int yearOfBirth, Long clubId,
                                                       List<CompetitionRankingResult> results) {

            public int totalPoints() {
                return results.stream().mapToInt(CompetitionRankingResult::points).sum();
            }

            public List<CompetitionRankingResult> sortedResults() {
                results.sort(comparingInt(CompetitionRankingResult::position));
                return results;
            }

            public static record CompetitionRankingResult(String eventAbbreviation, String result, int points, int position) {
            }

        }

    }

}
