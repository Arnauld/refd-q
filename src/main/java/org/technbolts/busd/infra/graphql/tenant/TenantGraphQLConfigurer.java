package org.technbolts.busd.infra.graphql.tenant;

import graphql.schema.DataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import org.technbolts.busd.core.tenants.Tenant;
import org.technbolts.busd.core.tenants.Tenants;
import org.technbolts.busd.infra.graphql.GraphQLConfigurer;
import org.technbolts.busd.infra.graphql.QueryContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class TenantGraphQLConfigurer implements GraphQLConfigurer {

    private final Tenants tenants;

    @Inject
    public TenantGraphQLConfigurer(Tenants tenants) {
        this.tenants = tenants;
    }

    @Override
    public RuntimeWiring.Builder configure(RuntimeWiring.Builder builder) {
        return builder.type(QUERY, this::configureQuery);

    }

    public TypeRuntimeWiring.Builder configureQuery(TypeRuntimeWiring.Builder builder) {
        return builder.dataFetcher("tenants", (DataFetcher<CompletableFuture<List<TenantGQL>>>) env -> {
            String glob = env.getArgument("glob");
            QueryContext context = env.getContext();
            return tenants.list(context)
                    .map(this::toGraphQL)
                    .collectItems().asList().subscribe().asCompletionStage();
        });
    }

    private TenantGQL toGraphQL(Tenant tenant) {
        return new TenantGQL(tenant);
    }

}
