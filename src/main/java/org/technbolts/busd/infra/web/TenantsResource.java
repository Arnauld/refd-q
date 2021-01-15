package org.technbolts.busd.infra.web;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.technbolts.busd.core.health.Health;
import org.technbolts.busd.core.health.HealthService;
import org.technbolts.busd.core.tenants.Tenant;
import org.technbolts.busd.core.tenants.Tenants;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/tenants")
public class TenantsResource {

    private final Tenants tenants;

    @Inject
    public TenantsResource(Tenants tenants) {
        this.tenants = tenants;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<TenantDTO> list() {
        return tenants.list().map(this::toDTO);
    }

    private TenantDTO toDTO(Tenant tenant) {
        return new TenantDTO(
                tenant.id().raw(),
                tenant.name(),
                tenant.createdAt().toEpochMilli());
    }

}