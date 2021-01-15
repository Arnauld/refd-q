package org.technbolts.busd.core.tenants;

import io.smallrye.common.constraint.Nullable;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface Tenants {
    Multi<Tenant> list();

    Uni<TenantId> create(@Nullable Integer id, String name);
}
