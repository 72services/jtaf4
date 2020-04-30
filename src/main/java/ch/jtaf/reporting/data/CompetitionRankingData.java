package ch.jtaf.reporting.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CompetitionRankingData {

    private final String name;
    private final LocalDate competitionDate;
    private final boolean alwaysFirstThreeMedals;
    private final int medalPercentage;

    private final List<CompetitionRankingCategory> categories = new ArrayList<>();

    public CompetitionRankingData(String name, LocalDate competitionDate, boolean alwaysFirstThreeMedals, int medalPercentage) {
        this.name = name;
        this.competitionDate = competitionDate;
        this.alwaysFirstThreeMedals = alwaysFirstThreeMedals;
        this.medalPercentage = medalPercentage;
    }

    public String getName() {
        return name;
    }

    public LocalDate getCompetitionDate() {
        return competitionDate;
    }

    public boolean isAlwaysFirstThreeMedals() {
        return alwaysFirstThreeMedals;
    }

    public int getMedalPercentage() {
        return medalPercentage;
    }

    public List<CompetitionRankingCategory> getCategories() {
        return categories;
    }
}
