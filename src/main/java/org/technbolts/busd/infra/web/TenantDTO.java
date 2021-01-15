package org.technbolts.busd.infra.web;

public class TenantDTO {
    public final int id;
    public final String name;
    public final long createdAt;

    public TenantDTO(int id, String name, long createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }
}
