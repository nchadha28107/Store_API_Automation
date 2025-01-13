package com.store.utils;

import com.store.enums.Context;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

    private static final Map<String, Object> scenarioContext = new HashMap<>();

    // Set a value in the context
    public static void setContext(Context key, Object value) {
        scenarioContext.put(key.toString(), value);
    }

    // Get a value from the context
    public static <T> T getContext(Context key) {
        return (T) scenarioContext.get(key.toString());
    }

    // Clear the context at the end of the scenario if necessary
    public static void clearContext() {
        scenarioContext.clear();
    }
}

