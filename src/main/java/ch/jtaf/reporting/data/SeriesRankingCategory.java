package ch.jtaf.reporting.data;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.compare;
import static java.util.stream.Collectors.toList;

public class SeriesRankingCategory {

    private final Long id;
    private final String abbreviation;
    private final String name;
    private final int yearFrom;
    private final int yearTo;
    private final int numberOfCompetitions;

    public SeriesRankingCategory(Long id, String abbreviation, String name, int yearFrom, int yearTo, int numberOfCompetitions) {
        this.id = id;
        this.abbreviation = abbreviation;
        this.name = name;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
        this.numberOfCompetitions = numberOfCompetitions;
    }

    private final List<SeriesRankingAthlete> athletes = new ArrayList<>();

    public List<SeriesRankingAthlete> getAthletes() {
        List<SeriesRankingAthlete> filteredAthletes = athletes.stream()
                .filter(seriesRankingAthlete -> seriesRankingAthlete.getResults().size() == numberOfCompetitions)
                .sorted((o1, o2) -> compare(o2.getTotalPoints(), o1.getTotalPoints()))
                .collect(toList());

        int i = 0;
        for (SeriesRankingAthlete athlete : filteredAthletes) {
            athlete.setRank(++i);
        }
        return filteredAthletes;
    }

    public void addAthlete(SeriesRankingAthlete athlete) {
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

    public int getNumberOfCompetitions() {
        return numberOfCompetitions;
    }
}
