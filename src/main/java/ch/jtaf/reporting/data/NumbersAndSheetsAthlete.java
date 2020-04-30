package ch.jtaf.reporting.data;

import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparingInt;

public class NumbersAndSheetsAthlete {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final int yearOfBirth;
    private final String category;
    private final String club;

    private final List<NumbersAndSheetsEvent> events = new ArrayList<>();

    public NumbersAndSheetsAthlete(Long id, String firstName, String lastName, int yearOfBirth, String category, String club) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.yearOfBirth = yearOfBirth;
        this.category = category;
        this.club = club;
    }

    public List<NumbersAndSheetsEvent> getEvents() {
        events.sort(comparingInt(NumbersAndSheetsEvent::getPosition));
        return events;
    }

    public Long getId() {
        return id;
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

    public String getClub() {
        return club;
    }
}
