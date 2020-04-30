package ch.jtaf.reporting.data;

import ch.jtaf.model.EventType;

import java.util.ArrayList;
import java.util.List;

public class EventsRankingEvent {

    private final String abbreviation;
    private final String gender;

    private final List<EventsRankingResult> results = new ArrayList<>();

    public EventsRankingEvent(String abbreviation, String gender) {
        this.abbreviation = abbreviation;
        this.gender = gender;
    }

    public List<EventsRankingResult> getResults() {
        results.sort((o1, o2) -> {
            if (o1.getEventType().equals(EventType.JUMP_THROW.name())) {
                // Higher results are better
                return Double.compare(o2.getResultAsDouble(), o1.getResultAsDouble());
            } else {
                // Lower results are better
                return Double.compare(o1.getResultAsDouble(), o2.getResultAsDouble());
            }
        });
        return results;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getGender() {
        return gender;
    }
}
