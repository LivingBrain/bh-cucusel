package com.brainhatchery.cucusel.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class TestContext {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<Object, Object> commonContext = new HashMap<>();

    public void setContextItem(Object key, Object value) {
        commonContext.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getContextItem(String keyName) {
        return (T) commonContext.get(keyName);
    }
}
