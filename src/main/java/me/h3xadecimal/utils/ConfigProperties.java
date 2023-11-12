package me.h3xadecimal.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

public class ConfigProperties {
    public static final String KEY_REFRESH_INTERVAL = "refresh-interval";
    public static final String KEY_MAX_COUNT = "max-count";
    public static final String KEY_MIN_COUNT = "min-count";

    private final Properties prop = new Properties();

    public ConfigProperties() {
        prop.setProperty("refresh-interval", "");
    }

    public void load(Path path) throws IOException {
        prop.load(Files.newInputStream(path));
    }

    public String dump() throws IOException {
        StringWriter sw = new StringWriter();
        prop.store(sw, "");
        return sw.toString();
    }

    public int getRefreshInterval() {
        try {
            return Integer.parseInt(prop.getProperty(KEY_REFRESH_INTERVAL, "150"));
        } catch (NumberFormatException e) {
            prop.setProperty(KEY_REFRESH_INTERVAL, "150");
            return 150;
        }
    }

    public void setRefreshInterval(int value) {
        prop.setProperty(KEY_REFRESH_INTERVAL, String.valueOf(Objects.requireNonNullElse(value, 150)));
    }

    public int getMaxCount() {
        try {
            return Integer.parseInt(prop.getProperty(KEY_MAX_COUNT, "16"));
        } catch (NumberFormatException e) {
            prop.setProperty(KEY_MAX_COUNT, "16");
            return 16;
        }
    }

    public void setMaxCount(int value) {
        prop.setProperty(KEY_MAX_COUNT, String.valueOf(Objects.requireNonNullElse(value, 16)));
    }

    public int getMinCount() {
        try {
            return Integer.parseInt(prop.getProperty(KEY_MIN_COUNT, "4"));
        } catch (NumberFormatException e) {
            prop.setProperty(KEY_MIN_COUNT, "4");
            return 4;
        }
    }

    public void setMinCount(int value) {
        prop.setProperty(KEY_MIN_COUNT, String.valueOf(Objects.requireNonNullElse(value, 4)));
    }
}
