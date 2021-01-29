package org.technbolts.busd.core;

public class Caller {
    public static Caller caller(String id, Type type) {
        if (id == null)
            throw new IllegalArgumentException("id cannot be null");
        if (type == null)
            throw new IllegalArgumentException("type cannot be null");
        return new Caller(id, type);
    }

    public enum Type {
        AGENT,
        SERVICE,
        CUSTOMER
    }

    private final String id;
    private final Type type;

    private Caller(String id, Type type) {
        this.id = id;
        this.type = type;
    }

    public String id() {
        return id;
    }

    public Type type() {
        return type;
    }
}
