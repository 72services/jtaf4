package ch.jtaf.reporting.data;

import java.util.ArrayList;
import java.util.List;

public class CompetitionRankingCategory {

    private final Long id;
    private final String abbreviation;
    private final String name;
    private final int yearFrom;
    private final int yearTo;

    private final List<CompetitionRankingAthlete> athletes = new ArrayList<>();

    public CompetitionRankingCategory(Long id, String abbreviation, String name, int yearFrom, int yearTo) {
        this.id = id;
        this.abbreviation = abbreviation;
        this.name = name;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
    }

    public List<CompetitionRankingAthlete> getAthletes() {
        athletes.sort((o1, o2) -> Integer.compare(o2.getTotalPoints(), o1.getTotalPoints()));

        int i = 0;
        for (CompetitionRankingAthlete athlete : athletes) {
            athlete.setRank(++i);
        }
        return athletes;
    }

    public void addAthlete(CompetitionRankingAthlete athlete) {
        athletes.add(athlete);
    }

    public Long getId() {
        return id;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    public int getYearFrom() {
        return yearFrom;
    }

    public int getYearTo() {
        return yearTo;
    }
}
