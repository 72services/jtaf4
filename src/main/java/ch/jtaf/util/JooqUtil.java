package ch.jtaf.util;

import org.jooq.Field;

public class JooqUtil {

    private JooqUtil() {
    }

    public static String fieldToCamelCase(Field<?> field) {
        StringBuilder sb = new StringBuilder();
        char[] chars = field.getName().toLowerCase().toCharArray();
        boolean wasUnderScore = false;
        for (char c : chars) {
            if (c == '_') {
                wasUnderScore = true;
            } else {
                if (wasUnderScore) {
                    sb.append(Character.toString(c).toUpperCase());
                } else {
                    sb.append(c);
                }
                wasUnderScore = false;
            }
        }
        return sb.toString();
    }
}
