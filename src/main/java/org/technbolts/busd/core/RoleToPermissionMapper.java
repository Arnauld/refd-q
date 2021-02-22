package org.technbolts.busd.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.technbolts.busd.core.Permission.*;

public class RoleToPermissionMapper {

    public static Set<Permission> permissionsFor(String role) {
        if (role.equalsIgnoreCase("ROOT")) {
            return new HashSet<>(asList(
                    TENANT_CREATE,
                    TENANT_READ,
                    TENANT_UPDATE,
                    AUDIT_META_READ,
                    AUTHORITY_CREATE));
        }
        if (role.equalsIgnoreCase("AGENT")) {
            return new HashSet<>(asList(AUDIT_META_READ,
                    AUTHORITY_CREATE,
                    AUTHORITY_UPDATE,
                    AUTHORITY_READ,
                    OPERATOR_CREATE,
                    OPERATOR_READ,
                    OPERATOR_UPDATE,
                    TRANSPORT_MODE_CREATE,
                    TRANSPORT_MODE_READ,
                    TRANSPORT_MODE_UPDATE,
                    VEHICLE_CREATE,
                    VEHICLE_READ,
                    VEHICLE_UPDATE));
        }
        return Collections.emptySet();
    }
}
