package org.technbolts.busd.core;

import org.technbolts.busd.core.tenants.TenantId;

public class ExecutionContext {
    private final TenantId tenantId;
    private final Caller caller;

    public ExecutionContext(TenantId tenantId, Caller caller) {
        this.tenantId = tenantId;
        this.caller = caller;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public Caller caller() {
        return caller;
    }
}
