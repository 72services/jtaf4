package ch.jtaf.reporting.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.lang.Integer.compare;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public record SeriesRankingData(String name, int numberOfCompetitions, List<Category> categories) {

    public static record Category(Long id, String abbreviation, String name, int yearFrom, int yearTo,
                                  List<Athlete> athletes) {

        public List<Athlete> getFilteredAthletes(int numberOfCompetitions) {
            return athletes.stream()
                .filter(athlete -> athlete.sortedResults().size() == numberOfCompetitions)
                .sorted((o1, o2) -> compare(o2.totalPoints(), o1.totalPoints()))
                .collect(toList());
        }

        public static record Athlete(Long id, String firstName, String lastName, int yearOfBirth, Long clubId,
                                     List<Result> results) {

            public int totalPoints() {
                return results.stream().map(Result::points).mapToInt(BigDecimal::intValue).sum();
            }

            public List<Result> sortedResults() {
                results.sort(comparing(Result::competitionDate));
                return results;
            }

            public static record Result(Long athleteId, Long competitionId, String competitionName, LocalDate competitionDate,
                                        BigDecimal points) {
            }

        }

    }

}
