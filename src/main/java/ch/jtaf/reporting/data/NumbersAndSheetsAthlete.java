package ch.jtaf.reporting.data;

import java.util.List;

import static java.util.Comparator.comparingInt;

public record NumbersAndSheetsAthlete(Long id, String firstName, String lastName, int yearOfBirth, String category, String club,
                                      List<NumbersAndSheetsEvent> events) {

    public List<NumbersAndSheetsEvent> sortedEvents() {
        events.sort(comparingInt(NumbersAndSheetsEvent::position));
        return events;
    }
}
