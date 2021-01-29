package org.technbolts.busd.infra.db;

import io.smallrye.common.constraint.Nullable;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.technbolts.busd.core.ExecutionContext;
import org.technbolts.busd.core.tenants.Tenant;
import org.technbolts.busd.core.tenants.TenantId;
import org.technbolts.busd.core.tenants.Tenants;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.OffsetDateTime;

import static org.technbolts.busd.core.tenants.TenantId.tenantId;

@ApplicationScoped
public class PgPoolTenants implements Tenants {


    private final ContextualizedPgPool pgPool;

    @Inject
    public PgPoolTenants(ContextualizedPgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Multi<Tenant> list(ExecutionContext context) {
        return pgPool.connection(context)
                .toMulti()
                .flatMap(conn ->
                        conn.query("select * from tenants")
                                .execute()
                                .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                                .onItem().transform(this::toTenant));

    }

    @Override
    public Uni<TenantId> create(ExecutionContext context, @Nullable Integer id, String name) {
        Uni<RowSet<Row>> query;
        if (id == null)
            query = pgPool.connection(context)
                    .onItem().transformToUni(conn ->
                            conn.preparedQuery("insert into tenants (name) values ($1) returning id")
                                    .execute(Tuple.of(name)));
        else
            query = pgPool.connection(context)
                    .onItem().transformToUni(conn ->
                            conn.preparedQuery("insert into tenants (id, name) values ($1, $2) returning id")
                                    .execute(Tuple.of(id, name)));

        return query
                .onItem().transformToUni(DbHelpers::singleResult)
                .map(this::toTenantId);
    }

    private TenantId toTenantId(Row row) {
        return tenantId(row.getInteger("id"));
    }

    private Tenant toTenant(Row row) {
        return new Tenant(
                tenantId(row.getInteger("id")),
                row.getString("name"),
                toInstant(row.getOffsetDateTime("created_at")));
    }

    private static Instant toInstant(OffsetDateTime dateTime) {
        if (dateTime == null)
            return null;
        return dateTime.toInstant();
    }
}
