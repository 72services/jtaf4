package ch.jtaf.reporting.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventsRankingData {

    private final String name;
    private final LocalDate competitionDate;

    private final List<EventsRankingEvent> events = new ArrayList<>();

    public EventsRankingData(String name, LocalDate competitionDate) {
        this.name = name;
        this.competitionDate = competitionDate;
    }

    public String getName() {
        return name;
    }

    public LocalDate getCompetitionDate() {
        return competitionDate;
    }

    public List<EventsRankingEvent> getEvents() {
        return events;
    }
}
