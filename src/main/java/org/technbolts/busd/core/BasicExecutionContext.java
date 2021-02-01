package org.technbolts.busd.core;

import org.technbolts.busd.core.tenants.TenantId;

import java.util.Set;

public class BasicExecutionContext implements ExecutionContext {
    private final TenantId tenantId;
    private final Caller caller;
    private final Set<String> roles;

    public BasicExecutionContext(TenantId tenantId, Caller caller, Set<String> roles) {
        this.tenantId = tenantId;
        this.caller = caller;
        this.roles = roles;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public Caller caller() {
        return caller;
    }

    @Override
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
