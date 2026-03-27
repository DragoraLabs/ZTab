package dev.example.ztabfusion.config;

import java.util.Map;

public record GroupRule(String key, String permission, int priority, String prefix, String suffix) {
    public static GroupRule fromMap(Map<?, ?> raw, int index) {
        String key = readString(raw.get("key"), "group" + index);
        String permission = readString(raw.get("permission"), "");
        int priority = readInt(raw.get("priority"), 100);
        String prefix = readString(raw.get("prefix"), "");
        String suffix = readString(raw.get("suffix"), "");
        return new GroupRule(key, permission, priority, prefix, suffix);
    }

    private static String readString(Object raw, String fallback) {
        if (raw == null) {
            return fallback;
        }
        String normalized = String.valueOf(raw).trim();
        return normalized.isEmpty() ? fallback : normalized;
    }

    private static int readInt(Object raw, int fallback) {
        if (raw == null) {
            return fallback;
        }
        if (raw instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(raw).trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
