package ch.jtaf.reporting.data;

import java.math.BigDecimal;

public class ClubResultData {

    private int rank;
    private final String club;
    private final BigDecimal points;

    public ClubResultData(String club, BigDecimal points) {
        this.club = club;
        this.points = points;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public String getClub() {
        return club;
    }

    public BigDecimal getPoints() {
        return points;
    }
}
