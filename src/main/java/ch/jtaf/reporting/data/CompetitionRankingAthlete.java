package ch.jtaf.reporting.data;

import static java.util.Comparator.comparingInt;

import java.util.ArrayList;
import java.util.List;

public class CompetitionRankingAthlete {

    private int rank;
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final int yearOfBirth;
    private final Long clubId;

    public CompetitionRankingAthlete(Long id, String firstName, String lastName, int yearOfBirth, Long clubId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.yearOfBirth = yearOfBirth;
        this.clubId = clubId;
    }

    private final List<CompetitionRankingResult> results = new ArrayList<>();

    public int getTotalPoints() {
        return results.stream().mapToInt(CompetitionRankingResult::getPoints).sum();
    }

    public List<CompetitionRankingResult> getResults() {
        results.sort(comparingInt(CompetitionRankingResult::getPosition));
        return results;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void addResult(CompetitionRankingResult result) {
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
