package org.technbolts.busd.infra.graphql.tenant;

import graphql.schema.DataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import org.jboss.logging.Logger;
import org.technbolts.busd.core.tenants.NewTenant;
import org.technbolts.busd.core.tenants.Tenant;
import org.technbolts.busd.core.tenants.Tenants;
import org.technbolts.busd.infra.graphql.ConnectionGQL;
import org.technbolts.busd.infra.graphql.GraphQLConfigurer;
import org.technbolts.busd.infra.graphql.Mappers;
import org.technbolts.busd.infra.graphql.QueryContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static org.technbolts.busd.infra.graphql.Mappers.toConnectionGQLUsing;
import static org.technbolts.busd.infra.graphql.PayloadErrorResolver.payloadErrorResolver;

@ApplicationScoped
public class TenantGraphQLConfigurer implements GraphQLConfigurer {

    private static final Logger LOG = Logger.getLogger(TenantGraphQLConfigurer.class);
    private final Tenants tenants;

    @Inject
    public TenantGraphQLConfigurer(Tenants tenants) {
        this.tenants = tenants;
    }

    @Override
    public RuntimeWiring.Builder configure(RuntimeWiring.Builder builder) {
        builder = configureResolvers(builder);
        return builder
                .type(QUERY, this::configureQuery)
                .type(MUTATION, this::configureMutation);

    }

    private RuntimeWiring.Builder configureResolvers(RuntimeWiring.Builder builder) {
        for (GraphQLConfigurer resolver : Arrays.asList(
                payloadErrorResolver("CreateTenantPayload", "Tenant"),
                payloadErrorResolver("TenantsPayload", "TenantsConnection"))) {
            builder = resolver.configure(builder);
        }
        return builder;
    }

    public TypeRuntimeWiring.Builder configureMutation(TypeRuntimeWiring.Builder builder) {
        return builder
                .dataFetcher("createTenant", (DataFetcher<CompletableFuture<Object>>) env -> {
                    Map<String, Object> input = env.getArgument("input");
                    NewTenant newTenant = toNewTenant(input);
                    QueryContext context = env.getContext();
                    return tenants.create(context, newTenant)
                            .flatMap(id -> tenants.findById(context, id))
                            .map(this::toTenantGQL)
                            .map(o -> (Object) o)
                            .onFailure().recoverWithItem(Mappers::toError)
                            .subscribeAsCompletionStage();
                });
    }

    private NewTenant toNewTenant(Map<String, Object> input) {
        String code = (String) input.get("code");
        String name = (String) input.get("name");
        return new NewTenant(null, code, name);
    }

    public TypeRuntimeWiring.Builder configureQuery(TypeRuntimeWiring.Builder builder) {
        return builder.dataFetcher("tenants", (DataFetcher<CompletableFuture<ConnectionGQL<TenantGQL>>>) env -> {
            String glob = env.getArgument("glob");
            QueryContext context = env.getContext();
            LOG.infof("Querying tenants using glob '%s'", glob);
            return tenants.all(context)
                    .map(toConnectionGQLUsing(this::toTenantGQL))
                    .map(ls -> {
                        LOG.infof("Querying tenants using glob '%s': %s", glob, ls);
                        return ls;
                    })
                    .subscribe().asCompletionStage();
        });
    }

    private TenantGQL toTenantGQL(Tenant tenant) {
        return new TenantGQL(tenant);
    }

}
