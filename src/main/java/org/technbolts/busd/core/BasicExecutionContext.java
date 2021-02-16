package org.technbolts.busd.core;

import org.jboss.logging.Logger;
import org.technbolts.busd.core.tenants.TenantId;

import java.util.Set;

public class BasicExecutionContext implements ExecutionContext {
    private static final Logger LOG = Logger.getLogger(BasicExecutionContext.class);

    private final TenantId tenantId;
    private final Caller caller;
    private final Set<Permission> permissions;

    public BasicExecutionContext(TenantId tenantId, Caller caller, Set<Permission> permissions) {
        this.tenantId = tenantId;
        this.caller = caller;
        this.permissions = permissions;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public Caller caller() {
        return caller;
    }

    @Override
    public boolean hasPermission(String permission) {
        try {
            Permission p = Permission.valueOf(permission);
            return permissions.contains(p);
        } catch (Exception e) {
            LOG.warnf("Invalid permission provided got: '%s'", permission);
            return false;
        }
    }
}
