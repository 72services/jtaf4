package ch.jtaf.reporting.data;

public class NumbersAndSheetsEvent {

    private final String abbreviation;
    private final String name;
    private final String gender;
    private final String type;
    private final int position;

    public NumbersAndSheetsEvent(String abbreviation, String name, String gender, String type, int position) {
        this.abbreviation = abbreviation;
        this.name = name;
        this.gender = gender;
        this.type = type;
        this.position = position;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getType() {
        return type;
    }

    public int getPosition() {
        return position;
    }
}
