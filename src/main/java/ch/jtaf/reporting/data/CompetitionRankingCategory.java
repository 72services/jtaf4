package ch.jtaf.reporting.data;

import java.util.List;

public record CompetitionRankingCategory(Long id, String abbreviation, String name, int yearFrom, int yearTo,
                                         List<CompetitionRankingAthlete> athletes) {

    public List<CompetitionRankingAthlete> sortedAthletes() {
        athletes.sort((o1, o2) -> Integer.compare(o2.totalPoints(), o1.totalPoints()));
        return athletes;
    }

}
