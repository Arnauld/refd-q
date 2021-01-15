package org.technbolts.busd.infra.db;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import org.technbolts.busd.core.tenants.Tenant;
import org.technbolts.busd.core.tenants.Tenants;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.technbolts.busd.core.tenants.TenantId.tenantId;

@ApplicationScoped
public class PgPoolTenants implements Tenants {

    private final PgPool pgPool;

    @Inject
    public PgPoolTenants(PgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Multi<Tenant> list() {
        return pgPool.query("select * from tenants")
                .execute()
                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(this::toTenant);

    }

    private Tenant toTenant(Row row) {
        return new Tenant(
                tenantId(row.getInteger("id")),
                row.getString("name"),
                row.getOffsetDateTime("created_at").toInstant());
    }
}
