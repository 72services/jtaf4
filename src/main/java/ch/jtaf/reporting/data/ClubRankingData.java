package ch.jtaf.reporting.data;

import java.util.List;

public record ClubRankingData(String seriesName, List<ClubResultData> results) {

    public List<ClubResultData> sortedResults() {
        results.sort((o1, o2) -> o2.points().compareTo(o1.points()));
        return results;
    }
}
