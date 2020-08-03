package ch.jtaf.reporting.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class SeriesRankingAthlete {

    private int rank;
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final int yearOfBirth;
    private final Long clubId;

    private final List<SeriesRankingResult> results = new ArrayList<>();

    public SeriesRankingAthlete(Long id, String firstName, String lastName, int yearOfBirth, Long clubId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.yearOfBirth = yearOfBirth;
        this.clubId = clubId;
    }

    public int getTotalPoints() {
        return results.stream().map(SeriesRankingResult::getPoints).mapToInt(BigDecimal::intValue).sum();
    }

    public List<SeriesRankingResult> getResults() {
        results.sort(comparing(SeriesRankingResult::getCompetitionDate));
        return results;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void addResult(SeriesRankingResult result) {
        results.add(result);
    }

    public int getRank() {
        return rank;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public Long getClubId() {
        return clubId;
    }
}
