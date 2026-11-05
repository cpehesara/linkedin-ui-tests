package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads settings from config.properties in the project root.
 * An environment variable (e.g. LINKEDIN_EMAIL for linkedin.email) or a
 * -D system property takes priority, which is handy for CI or quick overrides.
 */
public final class ConfigReader {

    private static final Properties PROPS = new Properties();

    static {
        try (FileInputStream in = new FileInputStream("config.properties")) {
            PROPS.load(in);
        } catch (IOException e) {
            throw new RuntimeException(
                "config.properties not found in the project root. "
                + "Copy config.properties.example to config.properties and fill in your test account.", e);
        }
    }

    private ConfigReader() { }

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys;

        String env = System.getenv(key.toUpperCase().replace('.', '_'));
        if (env != null && !env.isBlank()) return env;

        return PROPS.getProperty(key);
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key).trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
