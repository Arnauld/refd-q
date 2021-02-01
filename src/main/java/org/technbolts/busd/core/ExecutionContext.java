package org.technbolts.busd.core;

import org.technbolts.busd.core.tenants.TenantId;

public interface ExecutionContext {
    TenantId tenantId();

    Caller caller();

    boolean hasRole(String role);
}
