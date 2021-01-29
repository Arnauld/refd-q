package org.technbolts.busd.core.tenants;

import io.smallrye.common.constraint.Nullable;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.technbolts.busd.core.ExecutionContext;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface Tenants {
    Multi<Tenant> list(ExecutionContext context);

    Uni<TenantId> create(ExecutionContext context, @Nullable Integer id, String name);
}
