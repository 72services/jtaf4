package ch.jtaf.reporting.data;

import java.util.ArrayList;
import java.util.List;

public class SeriesRankingData {

    private final String name;
    private final int numberOfCompetitions;

    private final List<SeriesRankingCategory> categories = new ArrayList<>();

    public SeriesRankingData(String name, int numberOfCompetitions) {
        this.name = name;
        this.numberOfCompetitions = numberOfCompetitions;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfCompetitions() {
        return numberOfCompetitions;
    }

    public List<SeriesRankingCategory> getCategories() {
        return categories;
    }
}
