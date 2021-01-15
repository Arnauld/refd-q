package org.technbolts.busd.infra.db;

import io.smallrye.common.constraint.Nullable;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.technbolts.busd.core.tenants.Tenant;
import org.technbolts.busd.core.tenants.TenantId;
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

    @Override
    public Uni<TenantId> create(@Nullable Integer id, String name) {
        Uni<RowSet<Row>> query;
        if (id == null)
            query = pgPool.preparedQuery("insert into tenants (name) values ($1) returning id")
                    .execute(Tuple.of(name));
        else
            query = pgPool.preparedQuery("insert into tenants (id, name) values ($1, $2) returning id")
                    .execute(Tuple.of(id, name));

        return query
                .onItem().transformToUni(set -> {
                    RowIterator<Row> it = set.iterator();
                    if (it.hasNext())
                        return Uni.createFrom().item(it.next());
                    return Uni.createFrom().nothing();
                })
                .map(this::toTenantId);
    }

    private TenantId toTenantId(Row row) {
        return tenantId(row.getInteger("id"));
    }

    private Tenant toTenant(Row row) {
        return new Tenant(
                tenantId(row.getInteger("id")),
                row.getString("name"),
                row.getOffsetDateTime("created_at").toInstant());
    }
}
