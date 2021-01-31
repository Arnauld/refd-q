package org.technbolts.busd.infra.graphql;

import org.technbolts.busd.core.tenants.Tenant;

public class TenantGQL extends Adapter<Tenant> {
    public final int id;
    public final String code;
    public final String name;

    public TenantGQL(Tenant tenant) {
        super(tenant);
        this.id = tenant.id().raw();
        this.code = tenant.code();
        this.name = tenant.code();
    }
}
