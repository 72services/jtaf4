package ch.jtaf.reporting.data;

import ch.jtaf.model.EventType;

import java.util.List;

public record EventsRankingEvent(String abbreviation, String gender, List<EventsRankingResult> results) {

    public List<EventsRankingResult> sortedResults() {
        results.sort((o1, o2) -> {
            if (o1.eventType().equals(EventType.JUMP_THROW.name())) {
                // Higher results are better
                return Double.compare(o2.resultAsDouble(), o1.resultAsDouble());
            } else {
                // Lower results are better
                return Double.compare(o1.resultAsDouble(), o2.resultAsDouble());
            }
        });
        return results;
    }
}
