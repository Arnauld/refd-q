package org.technbolts.busd.core;

import java.util.HashMap;
import java.util.Map;

public class KeyValues {
    private final Map<String, String> raw;

    public KeyValues(Map<String, String> raw) {
        this.raw = new HashMap<>(raw);
    }

    public Map<String, Object> asMapOfObject() {
        return new HashMap<>(raw);
    }
}
