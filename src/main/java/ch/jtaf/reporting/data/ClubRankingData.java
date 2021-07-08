package ch.jtaf.reporting.data;

import java.util.List;

public record ClubRankingData(String seriesName, List<ClubResultData> results) {

    public List<ClubResultData> getResults() {
        results.sort((o1, o2) -> o2.getPoints().compareTo(o1.getPoints()));

        int rank = 0;
        for (ClubResultData result : results) {
            result.setRank(++rank);
        }

        return results;
    }
}
