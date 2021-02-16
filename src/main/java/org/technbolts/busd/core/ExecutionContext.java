package org.technbolts.busd.core;

import org.technbolts.busd.core.tenants.TenantId;

public interface ExecutionContext {
    ExecutionContext NONE = new ExecutionContext() {
        @Override
        public TenantId tenantId() {
            throw new InvalidContextException();
        }

        @Override
        public Caller caller() {
            throw new InvalidContextException();
        }

        @Override
        public boolean hasPermission(String permission) {
            return false;
        }
    };

    TenantId tenantId();

    Caller caller();

    boolean hasPermission(String permission);

    class InvalidContextException extends RuntimeException {
        public InvalidContextException() {
        }
    }
}
