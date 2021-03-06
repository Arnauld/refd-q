package org.technbolts.busd.infra.db;

import io.quarkus.reactive.datasource.ReactiveDataSource;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.SqlClientHelper;
import io.vertx.mutiny.sqlclient.SqlConnection;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jboss.logging.Logger;
import org.technbolts.busd.core.ExecutionContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;
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

    public <T> Uni<T> withConnectionUni(ExecutionContext context, Function<SqlConnection, Uni<? extends T>> func) {
        return SqlClientHelper.usingConnectionUni(pgPool, sqlConnection ->
                contextualize(context, sqlConnection).onItem().transformToUni(func));
    }

    public <T> Multi<T> withConnectionMulti(ExecutionContext context, Function<SqlConnection, Multi<? extends T>> func) {
        return SqlClientHelper.usingConnectionMulti(pgPool, sqlConnection ->
                contextualize(context, sqlConnection).onItem().transformToMulti(func));
    }

    protected Uni<SqlConnection> contextualize(final ExecutionContext context, final SqlConnection conn) {
        List<Tuple> tuples = asList(
                Tuple.of("var.caller_type", context.caller().type().name()),
                Tuple.of("var.caller_id", context.caller().id()),
                Tuple.of("var.tenant_id", context.tenantId().asString())
        );
        LOG.infof("tuples: %s", tuples.stream().map(Tuple::deepToString).collect(Collectors.toList()));
        return conn.preparedQuery("select set_config($1, $2, 'f');")
                .executeBatch(tuples)
                .map(r -> conn);
    }
}

