package org.technbolts.busd.infra.web.tenants;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.technbolts.busd.core.tenants.Tenant;
import org.technbolts.busd.core.tenants.TenantId;
import org.technbolts.busd.core.tenants.Tenants;
import org.technbolts.busd.infra.web.ErrorDTO;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

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
        return tenants
                .list()
                .map(this::toDTO);
    }

    @PUT
    public Uni<Response> create(TenantInputDTO input) {
        return tenants
                .create(input.id, input.name)
                .map(this::toDTO)
                .map(dto -> Response.created(URI.create("/api/v1/tenants/" + dto.id)).entity(dto).build())
                .onFailure()
                .recoverWithItem(throwable -> Response.status(BAD_REQUEST).entity(ErrorDTO.errorDTO(throwable)).build());
    }

    private TenantDTO toDTO(Tenant tenant) {
        return new TenantDTO(
                tenant.id().raw(),
                tenant.name(),
                tenant.createdAt().toEpochMilli());
    }


    private TenantIdDTO toDTO(TenantId tenantId) {
        return new TenantIdDTO(tenantId.raw());
    }

}