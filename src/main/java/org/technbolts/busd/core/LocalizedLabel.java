package org.technbolts.busd.core;

import java.util.HashMap;
import java.util.Map;

public class LocalizedLabel {
    private final Map<String, String> raw;

    public LocalizedLabel(Map<String, String> raw) {
        this.raw = new HashMap<>(raw);
    }

    public Map<String, Object> asMapOfObject() {
        return new HashMap<>(raw);
    }
}
