package org.technbolts.busd.core.health;

import io.smallrye.mutiny.Uni;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HealthService {
    private long startupMillis;

    @PostConstruct
    void init() {
        startupMillis = System.currentTimeMillis();
    }

    public Uni<Health> status() {
        return Uni.createFrom().item(new Health(Health.Status.OK, System.currentTimeMillis() - startupMillis));
    }
}
