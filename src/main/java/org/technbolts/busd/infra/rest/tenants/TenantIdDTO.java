package org.technbolts.busd.infra.rest.tenants;

import javax.json.bind.annotation.JsonbProperty;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class TenantIdDTO {

    @JsonbProperty("id")
    public final int id;

    public TenantIdDTO(int id) {
        this.id = id;
    }
}
