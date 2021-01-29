package org.technbolts.busd.infra.db;

import io.quarkus.reactive.datasource.ReactiveDataSource;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jboss.logging.Logger;
import org.technbolts.busd.core.ExecutionContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@ApplicationScoped
public class ContextualizedPgPool {

    private static final Logger LOG = Logger.getLogger(ContextualizedPgPool.class);
    private final PgPool pgPool;

    @Inject
    public ContextualizedPgPool(@ReactiveDataSource("app") PgPool pgPool) {
        this.pgPool = pgPool;
    }

    public Uni<SqlConnection> connection(ExecutionContext context) {
        return pgPool.getConnection()
                .onItem()
                .transformToUni(conn -> contextualize(context, conn));
    }

    public Uni<SqlConnection> contextualize(final ExecutionContext context, final SqlConnection conn) {
        List<Tuple> tuples = asList(
                Tuple.of("var.caller_type", context.caller().type().name()),
                Tuple.of("var.caller_id", context.caller().id()),
                Tuple.of("var.tenant_id", context.tenantId().asString())
        );
        LOG.infof("tuples: %s", tuples.stream().map(Tuple::deepToString).collect(Collectors.toList()));
        return conn.preparedQuery("select set_config($1, $2, 'f');")
                .executeBatch(tuples)
                .map(r -> {
                    r.iterator().forEachRemaining(row -> LOG.infof("'%s'", row.getString(1)));
                    return conn;
                });
    }
}

