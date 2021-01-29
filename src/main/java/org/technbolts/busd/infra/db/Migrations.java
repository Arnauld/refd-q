package org.technbolts.busd.infra.db;

import io.quarkus.flyway.FlywayDataSource;
import io.quarkus.runtime.StartupEvent;
import org.flywaydb.core.Flyway;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class Migrations {

    private static final Logger LOG = Logger.getLogger(Migrations.class);

    @Inject
    @FlywayDataSource("mig")
    Flyway flyway;

    void startup(@Observes StartupEvent event) {
        LOG.info("Starting migration");
        Flyway f = Flyway.configure()
                .configuration(this.flyway.getConfiguration())
                .initSql("SET ROLE busd_dev0")
                .load();
        f.clean();
        f.migrate();
        LOG.infof("Migration done, version: %s", f.info().current().getVersion().toString());
    }
}
