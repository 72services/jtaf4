package ch.jtaf.reporting.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventsRankingResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsRankingResult.class);

    private final String firstName;
    private final String lastName;
    private final int yearOfBirth;
    private final String category;
    private final Long clubId;
    private final String eventAbbrevation;
    private final String eventType;
    private final String result;

    public EventsRankingResult(String firstName, String lastName, int yearOfBirth, String category, Long clubId, String eventAbbrevation, String eventType, String result) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.yearOfBirth = yearOfBirth;
        this.category = category;
        this.clubId = clubId;
        this.eventAbbrevation = eventAbbrevation;
        this.eventType = eventType;
        this.result = result;
    }

    double getResultAsDouble() {
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public String getCategory() {
        return category;
    }

    public Long getClubId() {
        return clubId;
    }

    public String getEventAbbrevation() {
        return eventAbbrevation;
    }

    public String getEventType() {
        return eventType;
    }

    public String getResult() {
        return result;
    }
}
