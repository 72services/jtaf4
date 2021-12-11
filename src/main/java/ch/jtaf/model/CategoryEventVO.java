package ch.jtaf.model;

public record CategoryEventVO(String abbreviation, String name, String gender, String eventType,
                              String A, String B, String C, Integer position,
                              Long categoryId, Long eventId) {
}
