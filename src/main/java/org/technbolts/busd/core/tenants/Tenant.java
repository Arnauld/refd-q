package org.technbolts.busd.core.tenants;

import java.time.Instant;

public class Tenant {
    private final TenantId id;
    private final String name;
    private final Instant createdAt;

    public Tenant(TenantId id, String name, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public TenantId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
