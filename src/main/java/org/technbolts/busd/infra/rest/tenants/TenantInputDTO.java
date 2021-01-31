package org.technbolts.busd.infra.rest.tenants;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class TenantInputDTO {

    @JsonbProperty(value = "id", nillable = true)
    public final Integer id;

    @JsonbProperty("name")
    public final String name;

    @JsonbProperty("code")
    public final String code;

    @JsonbCreator
    public TenantInputDTO(@JsonbProperty(value = "id", nillable = true) Integer id,
                          @JsonbProperty("name") String name,
                          @JsonbProperty("code") String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
