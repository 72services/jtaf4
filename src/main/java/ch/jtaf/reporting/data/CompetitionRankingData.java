package ch.jtaf.reporting.data;

import java.time.LocalDate;
import java.util.List;

public record CompetitionRankingData(String name, LocalDate competitionDate, boolean alwaysFirstThreeMedals, int medalPercentage,
                                     List<Category> categories) {

    public static record Category(String abbreviation, String name, int yearFrom, int yearTo, List<Athlete> athletes) {

        public List<Athlete> sortedAthletes() {
            athletes.sort((o1, o2) -> Integer.compare(o2.totalPoints(), o1.totalPoints()));
            return athletes;
        }

        public static record Athlete(String firstName, String lastName, int yearOfBirth, String club, List<Result> results) {

            public int totalPoints() {
                if (results == null) {
                    return 0;
                } else {
                    return results.stream().mapToInt(Result::points).sum();
                }
            }

            public static record Result(String eventAbbreviation, String result, int points, int position) {
            }

        }

    }

}
