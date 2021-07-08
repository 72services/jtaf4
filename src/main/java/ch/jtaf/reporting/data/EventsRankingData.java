package ch.jtaf.reporting.data;

import ch.jtaf.model.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public record EventsRankingData(String name, LocalDate competitionDate, List<EventsRankingEvent> events) {

    public static record EventsRankingEvent(String abbreviation, String gender, List<Result> results) {

        public List<Result> sortedResults() {
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

        public static record Result(String firstName, String lastName, int yearOfBirth, String category, Long clubId,
                                    String eventAbbreviation, String eventType, String result) {

            private static final Logger LOGGER = LoggerFactory.getLogger(Result.class);

            double resultAsDouble() {
                try {
                    if (result == null || result.length() == 0) {
                        return 0.0d;
                    } else {
                        String[] parts = result.split("\\.");
                        if (parts.length == 3) {
                            return (Double.parseDouble(parts[0]) * 60) + Double.parseDouble(parts[1] + "." + parts[2]);
                        } else {
                            return Double.parseDouble(result);
                        }
                    }
                } catch (NumberFormatException e) {
                    LOGGER.error(e.getMessage(), e);
                    return 0.0d;
                }
            }

        }

    }

}
