package org.technbolts.busd.core.tenants;

public class TenantId {

    public static TenantId tenantId(int raw) {
        if (raw < 0)
            throw new IllegalArgumentException("Cannot be negative");
        return new TenantId(raw);
    }

    private final int raw;

    private TenantId(int raw) {
        this.raw = raw;
    }

    @Override
    public String toString() {
        return "TenantId{" + raw + '}';
    }

    public int raw() {
        return raw;
    }

    public String asString() {
        return String.valueOf(raw);
    }
}
