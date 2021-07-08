package ch.jtaf.reporting.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record EventsRankingResult(String firstName, String lastName, int yearOfBirth, String category, Long clubId,
                                  String eventAbbrevation, String eventType, String result) {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsRankingResult.class);

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
