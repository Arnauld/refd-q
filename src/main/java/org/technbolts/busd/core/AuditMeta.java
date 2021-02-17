package org.technbolts.busd.core;

import java.time.Instant;

public class AuditMeta implements HasAuditMeta {
    private final Instant createdAt;
    private final Caller createdBy;
    private final Instant updatedAt;
    private final Caller updatedBy;

    public AuditMeta(Instant createdAt, Caller createdBy, Instant updatedAt, Caller updatedBy) {
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    @Override
    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public Caller createdBy() {
        return createdBy;
    }

    @Override
    public Instant updatedAt() {
        return updatedAt;
    }

    @Override
    public Caller updatedBy() {
        return updatedBy;
    }
}
