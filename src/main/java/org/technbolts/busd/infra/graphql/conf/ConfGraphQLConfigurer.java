package org.technbolts.busd.infra.graphql.conf;

import graphql.schema.DataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import org.technbolts.busd.infra.graphql.GraphQLConfigurer;
import org.technbolts.busd.infra.graphql.QueryContext;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

@ApplicationScoped
public class ConfGraphQLConfigurer implements GraphQLConfigurer {

    private final List<OrganizationGQL> organizationGQLS = new ArrayList<>();

    @Override
    public RuntimeWiring.Builder configure(RuntimeWiring.Builder builder) {
        return builder.type(QUERY, this::configureQuery).type(MUTATION, this::configureMutation);

    }

    public TypeRuntimeWiring.Builder configureQuery(TypeRuntimeWiring.Builder builder) {
        return builder
                .dataFetcher("organization", (DataFetcher<CompletableFuture<OrganizationPayloadGQL>>) env -> {
                    int id = env.getArgument("id");
                    QueryContext context = env.getContext();
                    return completedFuture(organizationGQLS.stream()
                            .filter(o -> o.id == id)
                            .map(this::toGraphQL)
                            .map(o -> new OrganizationPayloadGQL(o, null))
                            .findFirst()
                            .orElseGet(() -> new OrganizationPayloadGQL(null, ErrorGQL.notFound())));
                });
    }

    public TypeRuntimeWiring.Builder configureMutation(TypeRuntimeWiring.Builder builder) {
        return builder
                .dataFetcher("createOrganization", (DataFetcher<CompletableFuture<CreateOrganizationPayloadGQL>>) env -> {
                    Map<String,Object> input = env.getArgument("input");
                    CreateOrganizationInputGQL inputGQL = CreateOrganizationInputGQL.fromMap(input);
                    QueryContext context = env.getContext();
                    return completedFuture(organizationGQLS.stream()
                            .filter(o -> o.id == 1)
                            .map(this::toGraphQL)
                            .map(o -> new CreateOrganizationPayloadGQL(o, null))
                            .findFirst()
                            .orElseGet(() -> new CreateOrganizationPayloadGQL(null, ErrorGQL.notFound())));
                });
    }

    private OrganizationGQL toGraphQL(OrganizationGQL oql) {
        return oql;
    }

}
