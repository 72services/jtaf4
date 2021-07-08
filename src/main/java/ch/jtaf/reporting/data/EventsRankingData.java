package ch.jtaf.reporting.data;

import java.time.LocalDate;
import java.util.List;

public record EventsRankingData(String name, LocalDate competitionDate, List<EventsRankingEvent> events) {
}
