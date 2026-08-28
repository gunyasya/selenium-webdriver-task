package config;

import model.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPERTIES = load();

    private ConfigReader() {
    }

    public static String getBaseUrl() {
        return PROPERTIES.getProperty("base.url");
    }

    public static User getDefaultUser() {
        return new User(PROPERTIES.getProperty("user.username"), PROPERTIES.getProperty("user.password"));
    }

    private static Properties load() {
        String env = System.getProperty("env", "qa");
        String resourcePath = "config/" + env + ".properties";
        Properties properties = new Properties();
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("Config file not found on classpath: " + resourcePath);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load config file: " + resourcePath, e);
        }
        return properties;
    }
}