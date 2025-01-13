package com.store.utils;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class ConfigReader {

    private static Map<String, Object> config;

    static {
        Yaml yaml = new Yaml();
        InputStream inputStream = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("endpoints.yml");

        if (inputStream == null) {
            throw new RuntimeException("Unable to load endpoints.yml file.");
        }
        config = yaml.load(inputStream);
    }

    public static String getBaseUrl(String environment) {
        // Accessing the 'environments' map and then the specific environment's base_url
        Map<String, Object> environments = (Map<String, Object>) config.get("environments");
        if (environments == null) {
            throw new RuntimeException("environments not found in config.");
        }
        Map<String, Object> environmentDetails = (Map<String, Object>) environments.get(environment);
        if (environmentDetails == null) {
            throw new RuntimeException("Environment " + environment + " not found.");
        }
        // Access the base_url for the specified environment
        return (String) environmentDetails.get("base_url");
    }

    public static String getEndpoint(String category, String action) {
        Map<String, Object> endpoints = (Map<String, Object>) config.get(category);
        return (String) endpoints.get(action);
    }
}
