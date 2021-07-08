package ch.jtaf.reporting.data;

import java.util.List;

public record SeriesRankingData(String name, int numberOfCompetitions, List<SeriesRankingCategory> categories) {

}
