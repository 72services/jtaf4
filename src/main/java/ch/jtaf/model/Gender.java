package ch.jtaf.model;

import java.util.ArrayList;
import java.util.List;

public enum Gender {
    M, F;

    public static List<String> valuesAsStrings() {
        List<String> valuesAsStrings = new ArrayList<>();
        for (Gender value : values()) {
            valuesAsStrings.add(value.name());
        }
        return valuesAsStrings;
    }
}
