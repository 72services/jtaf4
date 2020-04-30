package ch.jtaf.reporting.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SeriesRankingResult {

    private final Long athleteId;
    private final Long competionId;
    private final String competitionName;
    private final LocalDate competitionDate;
    private BigDecimal points;

    public SeriesRankingResult(Long athleteId, Long competionId, String competitionName, LocalDate competitionDate, BigDecimal points) {
        this.athleteId = athleteId;
        this.competionId = competionId;
        this.competitionName = competitionName;
        this.competitionDate = competitionDate;
        this.points = points;
    }

    public void addPoints(BigDecimal points) {
        this.points = this.points.add(points);
    }

    public Long getAthleteId() {
        return athleteId;
    }

    public Long getCompetionId() {
        return competionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public LocalDate getCompetitionDate() {
        return competitionDate;
    }

    public BigDecimal getPoints() {
        return points;
    }

    public void setPoints(BigDecimal points) {
        this.points = points;
    }
}
