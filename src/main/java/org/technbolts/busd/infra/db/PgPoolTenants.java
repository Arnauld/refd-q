package org.technbolts.busd.infra.db;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jboss.logging.Logger;
import org.technbolts.busd.core.ExecutionContext;
import org.technbolts.busd.core.tenants.NewTenant;
import org.technbolts.busd.core.tenants.Tenant;
import org.technbolts.busd.core.tenants.TenantId;
import org.technbolts.busd.core.tenants.Tenants;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import static org.technbolts.busd.core.tenants.TenantId.tenantId;
import static org.technbolts.busd.infra.db.DbHelpers.rowSetToListUsing;

@ApplicationScoped
public class PgPoolTenants implements Tenants {

    private static final Logger LOG = Logger.getLogger(PgPoolTenants.class);
    private final ContextualizedPgPool pgPool;

    @Inject
    public PgPoolTenants(ContextualizedPgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Multi<Tenant> list(ExecutionContext context) {
        return pgPool.withConnectionMulti(context, conn ->
                conn.query("select * from tenants")
                        .execute()
                        .onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                        .onItem().transform(this::toTenant)
                        .onItem().transform(t -> {
                            LOG.infof("Found tenant %s", t);
                            return t;
                })
        );
    }

    @Override
    public Uni<List<Tenant>> all(ExecutionContext context) {
        return pgPool.withConnectionUni(context, conn ->
                conn.query("select * from tenants")
                        .execute()
                        .map(rowSetToListUsing(this::toTenant))
        );
    }

    @Override
    public Uni<Tenant> findById(ExecutionContext context, TenantId id) {
        String sql = "select * from tenants where id = $1";
        Tuple args = Tuple.of(id.raw());
        return pgPool.withConnectionUni(context, conn ->
                conn.preparedQuery(sql)
                        .execute(args)
                        .onItem().transformToUni(DbHelpers::singleResult)
                        .map(this::toTenant));
    }

    @Override
    public Uni<TenantId> create(ExecutionContext context, NewTenant newTenant) {
        String sql;
        Tuple args;
        if (newTenant.id() == null) {
            sql = "insert into tenants (name, code) values ($1, $2) returning id";
            args = Tuple.of(newTenant.name(), newTenant.code());
        } else {
            sql = "insert into tenants (id, name, code) values ($1, $2, $3) returning id";
            args = Tuple.of(newTenant.id(), newTenant.name(), newTenant.code());
        }

        return pgPool.withConnectionUni(context, conn ->
                conn.preparedQuery(sql)
                        .execute(args)
                        .onItem().transformToUni(DbHelpers::singleResult)
                        .map(this::toTenantId)
        );
    }

    private TenantId toTenantId(Row row) {
        return tenantId(row.getInteger("id"));
    }

    private Tenant toTenant(Row row) {
        return new Tenant(
                tenantId(row.getInteger("id")),
                row.getString("code"),
                row.getString("name"));
    }

    private static Instant toInstant(OffsetDateTime dateTime) {
        if (dateTime == null)
            return null;
        return dateTime.toInstant();
    }
}
