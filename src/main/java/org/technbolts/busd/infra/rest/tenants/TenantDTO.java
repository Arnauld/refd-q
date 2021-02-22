package org.technbolts.busd.infra.rest.tenants;

import javax.json.bind.annotation.JsonbProperty;

public class TenantDTO {

    @JsonbProperty("id")
    public final int id;

    @JsonbProperty("name")
    public final String name;

    public TenantDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
