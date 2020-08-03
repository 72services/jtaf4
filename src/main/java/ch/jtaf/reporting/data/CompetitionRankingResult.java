package ch.jtaf.reporting.data;

public class CompetitionRankingResult {

    private final String eventAbbreviation;
    private final String result;
    private final int points;
    private final int position;

    public CompetitionRankingResult(String eventAbbreviation, String result, int points, int position) {
        this.eventAbbreviation = eventAbbreviation;
        this.result = result;
        this.points = points;
        this.position = position;
    }

    public String getEventAbbreviation() {
        return eventAbbreviation;
    }

    public String getResult() {
        return result;
    }

    public int getPoints() {
        return points;
    }

    public int getPosition() {
        return position;
    }
}
