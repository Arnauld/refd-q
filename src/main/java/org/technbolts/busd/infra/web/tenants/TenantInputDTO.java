package org.technbolts.busd.infra.web.tenants;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class TenantInputDTO {

    @JsonbProperty(value = "id", nillable = true)
    public final Integer id;

    @JsonbProperty("name")
    public final String name;

    @JsonbCreator
    public TenantInputDTO(@JsonbProperty(value = "id", nillable = true) Integer id,
                          @JsonbProperty("name") String name) {
        this.id = id;
        this.name = name;
    }
}
