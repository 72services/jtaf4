package ch.jtaf.reporting.data;

import java.time.LocalDate;

public class NumbersAndSheetsCompetition {

    private final Long id;
    private final String name;
    private final LocalDate competitionDate;

    public NumbersAndSheetsCompetition(Long id, String name, LocalDate competitionDate) {
        this.id = id;
        this.name = name;
        this.competitionDate = competitionDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getCompetitionDate() {
        return competitionDate;
    }
}
