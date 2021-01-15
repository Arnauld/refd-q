package org.technbolts.busd.infra.web.tenants;

import javax.json.bind.annotation.JsonbProperty;

public class TenantDTO {

    @JsonbProperty("id")
    public final int id;

    @JsonbProperty("name")
    public final String name;

    @JsonbProperty("createdAt")
    public final long createdAt;

    public TenantDTO(int id, String name, long createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }
}
