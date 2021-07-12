package ch.jtaf.reporting.data;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.Integer.compare;
import static java.util.stream.Collectors.toList;

public record SeriesRankingData(String name, int numberOfCompetitions, List<Category> categories) {

    public static record Category(String abbreviation, String name, int yearFrom, int yearTo, List<Athlete> athletes) {

        public List<Athlete> getFilteredAthletes(int numberOfCompetitions) {
            return athletes.stream()
                .filter(athlete -> athlete.results().size() == numberOfCompetitions)
                .sorted((o1, o2) -> compare(o2.totalPoints(), o1.totalPoints()))
                .collect(toList());
        }

        public static record Athlete(String firstName, String lastName, int yearOfBirth, String club, List<Result> results) {

            public int totalPoints() {
                return results.stream().map(Result::points).mapToInt(BigDecimal::intValue).sum();
            }

            public static record Result(String competitionName, BigDecimal points) {
            }

        }

    }

}
