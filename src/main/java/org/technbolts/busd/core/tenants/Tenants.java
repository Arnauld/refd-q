package org.technbolts.busd.core.tenants;

import io.smallrye.mutiny.Multi;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface Tenants {
    Multi<Tenant> list();
}
