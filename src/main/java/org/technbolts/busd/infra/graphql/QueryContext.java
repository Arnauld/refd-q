package org.technbolts.busd.infra.graphql;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.technbolts.busd.core.BasicExecutionContext;
import org.technbolts.busd.core.Caller;
import org.technbolts.busd.core.ExecutionContext;
import org.technbolts.busd.core.tenants.TenantId;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class QueryContext implements ExecutionContext {
    public static QueryContext newQueryContext(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();

        String xTenantId = request.getHeader("X-Tenant-ID");
        String xCallerId = request.getHeader("X-Caller-ID");
        String xCallerType = request.getHeader("X-Caller-Type");
        String xRoles = request.getHeader("X-Roles");

        // TODO handle parsing error + missing/invalid input
        ExecutionContext executionContext = new BasicExecutionContext(
                TenantId.tenantId(Integer.parseInt(xTenantId)),
                Caller.caller(xCallerId, Caller.Type.valueOf(xCallerType)),
                Stream.of(xRoles.split(",")).collect(Collectors.toSet())
        );
        return new QueryContext(routingContext, executionContext);
    }

    private final RoutingContext routingContext;
    private final ExecutionContext executionContext;

    protected QueryContext(RoutingContext routingContext, ExecutionContext executionContext) {
        this.routingContext = routingContext;
        this.executionContext = executionContext;
    }

    @Override
    public TenantId tenantId() {
        return executionContext.tenantId();
    }

    @Override
    public Caller caller() {
        return executionContext.caller();
    }

    @Override
    public boolean hasRole(String role) {
        return executionContext.hasRole(role);
    }

    public RoutingContext routingContext() {
        return routingContext;
    }
}