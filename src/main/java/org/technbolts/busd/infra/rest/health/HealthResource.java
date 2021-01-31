package org.technbolts.busd.infra.rest.health;

import io.smallrye.mutiny.Uni;
import org.technbolts.busd.core.health.Health;
import org.technbolts.busd.core.health.HealthService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/v1/health")
public class HealthResource {

    private final HealthService healthService;

    @Inject
    public HealthResource(HealthService healthService) {
        this.healthService = healthService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<HealthDTO> hello() {
        return healthService.status().map(this::toDTO);
    }

    private HealthDTO toDTO(Health health) {
        return new HealthDTO(health.status().name().toLowerCase(), health.uptime());
    }
}