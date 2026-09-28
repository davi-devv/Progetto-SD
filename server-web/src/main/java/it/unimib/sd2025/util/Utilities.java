package it.unimib.sd2025.util;

import it.unimib.sd2025.DatabaseConnection;
import it.unimib.sd2025.model.GenericQueries;
import it.unimib.sd2025.model.Queries;
import jakarta.ws.rs.core.Response;

public class Utilities {
    public static boolean CFCheck(String cf) {
        return (cf == null || cf.isEmpty() || cf.contains(String.valueOf(DatabaseConnection.DEF_SEPARATOR)) ||
                cf.equals(GenericQueries.getID()));
    }
    public static String sanitize(String s) {
        StringBuilder sanitized = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == DatabaseConnection.DEF_SEPARATOR) {
                sanitized.append("\\").append(c);
            } else if (c == DatabaseConnection.KV_SEPARATOR) {
                throw new IllegalArgumentException("Invalid character '"+DatabaseConnection.KV_SEPARATOR+"'");
            } else {
                sanitized.append(c);
            }
        }
        return sanitized.toString();
    }

    public static String[] sanitizeArray(String[] array) {
        String[] sanitized = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            sanitized[i] = sanitize(array[i]);
        }
        return sanitized;
    }
    public static String kvbuilder(String key, String value) {
        return "%s%c%s".formatted(sanitize(key), DatabaseConnection.KV_SEPARATOR, sanitize(value));
    }
    public static String kvbuilder(String key, float value) {
        return "%s%c%s".formatted(sanitize(key), DatabaseConnection.KV_SEPARATOR, String.valueOf(value));
    }
    public static String kvbuilder(String key, int value) {
        return "%s%c%d".formatted(sanitize(key), DatabaseConnection.KV_SEPARATOR, value);
    }
    public static String kvbuilder(String key, boolean value) {
        return "%s%c%s".formatted(key, DatabaseConnection.KV_SEPARATOR, Boolean.toString(value));
    }
    public static String kvbuilder(Queries key, String value) {
        return "%s%c%s".formatted(key.getQuery(), DatabaseConnection.KV_SEPARATOR, sanitize(value));
    }
    public static String kvbuilder(Queries key, float value) {
        return "%s%c%s".formatted(key.getQuery(), DatabaseConnection.KV_SEPARATOR, String.valueOf(value));
    }
    public static String kvbuilder(Queries key, int value) {
        return "%s%c%d".formatted(key.getQuery(), DatabaseConnection.KV_SEPARATOR, value);
    }
    public static String kvbuilder(Queries key, boolean value) {
        return "%s%c%s".formatted(key.getQuery(), DatabaseConnection.KV_SEPARATOR, Boolean.toString(value));
    }
}
