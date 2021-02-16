package org.technbolts.busd.core.organizations;

public class OperatorId {

    public static OperatorId operatorId(int raw) {
        if (raw < 0)
            throw new IllegalArgumentException("Cannot be negative");
        return new OperatorId(raw);
    }

    private final int raw;

    private OperatorId(int raw) {
        this.raw = raw;
    }

    @Override
    public String toString() {
        return "OperatorId{" + raw + '}';
    }

    public int raw() {
        return raw;
    }

    public String asString() {
        return String.valueOf(raw);
    }
}
