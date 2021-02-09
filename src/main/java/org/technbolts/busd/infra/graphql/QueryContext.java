package org.technbolts.busd.infra.graphql;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.jboss.logging.Logger;
import org.technbolts.busd.core.BasicExecutionContext;
import org.technbolts.busd.core.Caller;
import org.technbolts.busd.core.ExecutionContext;
import org.technbolts.busd.core.tenants.TenantId;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class QueryContext implements ExecutionContext {

    private static final Logger LOG = Logger.getLogger(QueryContext.class);

    public static QueryContext newQueryContext(RoutingContext routingContext) {
        try {
            HttpServerRequest request = routingContext.request();
            String xTenantId = request.getHeader("X-Tenant-ID");
            String xCallerId = request.getHeader("X-Caller-ID");
            String xCallerType = request.getHeader("X-Caller-Type");
            String xRoles = request.getHeader("X-Roles");

            ExecutionContext executionContext = new BasicExecutionContext(
                    parseTenantId(xTenantId).orElse(null),
                    parseCaller(xCallerId, xCallerType).orElse(null),
                    parseRoles(xRoles)
            );
            return new QueryContext(routingContext, executionContext);
        } catch (Exception e) {
            return new QueryContext(routingContext, ExecutionContext.NONE);
        }
    }

    private static Set<String> parseRoles(String xRoles) {
        if (xRoles == null)
            return Collections.emptySet();
        return Stream.of(xRoles.split(",")).collect(Collectors.toSet());
    }

    private static Optional<Caller> parseCaller(String xCallerId, String xCallerType) {
        if (xCallerId == null || xCallerType == null)
            return Optional.empty();
        try {
            return Optional.of(Caller.caller(xCallerId, Caller.Type.valueOf(xCallerType)));
        } catch (IllegalArgumentException iae) {
            LOG.warnf("Invalid caller type format '%s', must be one of %s", xCallerType, asList(Caller.Type.values()));
            return Optional.empty();
        }
    }

    private static Optional<TenantId> parseTenantId(String xTenantId) {
        if (xTenantId == null)
            return Optional.empty();
        try {
            int raw = Integer.parseInt(xTenantId);
            return Optional.of(TenantId.tenantId(raw));
        } catch (NumberFormatException nfe) {
            LOG.warnf("Invalid tenantId format '%s'", xTenantId);
            return Optional.empty();
        }
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
