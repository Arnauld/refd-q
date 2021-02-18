package org.technbolts.busd.infra.rest.tenants;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.logging.Logger;
import org.technbolts.busd.core.BasicExecutionContext;
import org.technbolts.busd.core.Caller;
import org.technbolts.busd.core.ExecutionContext;
import org.technbolts.busd.core.tenants.NewTenant;
import org.technbolts.busd.core.tenants.Tenant;
import org.technbolts.busd.core.tenants.TenantId;
import org.technbolts.busd.core.tenants.Tenants;
import org.technbolts.busd.infra.rest.ErrorDTO;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.Instant;
import java.util.HashSet;

import static java.lang.Integer.parseInt;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.technbolts.busd.core.Caller.Type.AGENT;
import static org.technbolts.busd.core.tenants.TenantId.tenantId;

@Path("/api/v1/tenants")
public class TenantsResource {

    private static final Logger LOG = Logger.getLogger(TenantsResource.class);
    private final Tenants tenants;

    @Inject
    public TenantsResource(Tenants tenants) {
        this.tenants = tenants;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<TenantDTO> list(@HeaderParam("X-Tenant-ID") String tenantId) {
        return tenants
                .list(executionContext(tenantId))
                .map(this::toDTO);
    }

    private ExecutionContext executionContext(String tenantIdAsString) {
        TenantId tenantId = tenantId(parseInt(tenantIdAsString));
        return new BasicExecutionContext(tenantId, Caller.caller("a007", AGENT), new HashSet<>());
    }

    @PUT
    public Uni<Response> create(@HeaderParam("X-Tenant-ID") String tenantId, TenantInputDTO input) {
        return tenants
                .create(executionContext(tenantId), toNewTenant(input))
                .map(this::toDTO)
                .map(dto -> Response.created(URI.create("/api/v1/tenants/" + dto.id)).entity(dto).build())
                .onFailure()
                .recoverWithItem(throwable -> {
                    LOG.error("Oops", throwable);
                    return Response.status(BAD_REQUEST).entity(ErrorDTO.errorDTO(throwable)).build();
                });
    }

    private NewTenant toNewTenant(TenantInputDTO input) {
        return new NewTenant(input.id, input.name, input.code);
    }

    private TenantDTO toDTO(Tenant tenant) {
        return new TenantDTO(
                tenant.id().raw(),
                tenant.name());
    }

    private static Long toEpochMilli(Instant instant) {
        if (instant == null)
            return null;
        return instant.toEpochMilli();
    }

    private TenantIdDTO toDTO(TenantId tenantId) {
        return new TenantIdDTO(tenantId.raw());
    }

}