package ch.jtaf.reporting.data;

import java.util.List;

public record NumbersAndSheetsAthlete(Long id, String firstName, String lastName, Integer yearOfBirth, String category, String club,
                                      List<Event> events) {

    public static record Event(String abbreviation, String name, String gender, String type, int position) {
    }
}
