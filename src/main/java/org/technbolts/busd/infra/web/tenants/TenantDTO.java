package org.technbolts.busd.infra.web.tenants;

import javax.json.bind.annotation.JsonbProperty;

public class TenantDTO {

    @JsonbProperty("id")
    public final int id;

    @JsonbProperty("name")
    public final String name;

    @JsonbProperty("createdAt")
    public final Long createdAt;

    public TenantDTO(int id, String name, Long createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }
}
