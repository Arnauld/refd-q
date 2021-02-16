package org.technbolts.busd.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.technbolts.busd.core.Permission.*;
import static org.technbolts.busd.core.Permission.VEHICLE_WRITE;

public class RoleToPermissionMapper {

    public static Set<Permission> permissionsFor(String role) {
        if(role.equalsIgnoreCase("ROOT")) {
            return new HashSet<>(asList(TENANT_READ,
                    TENANT_WRITE,
                    AUDIT_META_READ,
                    AUTHORITY_CREATE));
        }
        if(role.equalsIgnoreCase("AGENT")) {
            return new HashSet<>(asList(AUDIT_META_READ,
                    AUTHORITY_CREATE,
                    AUTHORITY_WRITE,
                    AUTHORITY_READ,
                    OPERATOR_READ,
                    OPERATOR_WRITE,
                    TRANSPORT_MODE_READ,
                    TRANSPORT_MODE_WRITE,
                    VEHICLE_READ,
                    VEHICLE_WRITE));
        }
        return Collections.emptySet();
    }
}
