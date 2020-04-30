package ch.jtaf.reporting.data;

import java.util.ArrayList;
import java.util.List;

public class ClubRankingData {

    private final String seriesName;

    private final List<ClubResultData> results = new ArrayList<>();

    public ClubRankingData(String seriesName) {
        this.seriesName = seriesName;
    }

    public List<ClubResultData> getResults() {
        results.sort((o1, o2) -> o2.getPoints().compareTo(o1.getPoints()));

        int rank = 0;
        for (ClubResultData result : results) {
            result.setRank(++rank);
        }

        return results;
    }

    public String getSeriesName() {
        return seriesName;
    }
}
