package org.technbolts.busd.core.tenants;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.technbolts.busd.core.ExecutionContext;
import org.technbolts.busd.infra.graphql.QueryContext;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface Tenants {
    Multi<Tenant> list(ExecutionContext context);

    Uni<List<Tenant>> all(ExecutionContext context);

    Uni<TenantId> create(ExecutionContext context, NewTenant newTenant);

    Uni<Tenant> findById(ExecutionContext context, TenantId id);
}
