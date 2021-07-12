package ch.jtaf.reporting.data;

import java.util.List;

import static java.util.Comparator.comparingInt;

public record NumbersAndSheetsAthlete(Long id, String firstName, String lastName, int yearOfBirth, String category, String club,
                                      List<Event> events) {

    public static record Event(String abbreviation, String name, String gender, String type, int position) {
    }
}
