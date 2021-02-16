package org.technbolts.busd.infra.graphql.conf;

import graphql.schema.DataFetcher;
import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import io.smallrye.mutiny.tuples.Tuple2;
import org.technbolts.busd.infra.graphql.GraphQLConfigurer;
import org.technbolts.busd.infra.graphql.QueryContext;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

@ApplicationScoped
public class ConfGraphQLConfigurer implements GraphQLConfigurer {

    private final List<OrganizationGQL> organizationGQLS = new ArrayList<>();

    @Override
    public RuntimeWiring.Builder configure(RuntimeWiring.Builder builder) {
        builder = configureResolvers(builder);
        return builder
                .type(QUERY, this::configureQuery)
                .type(MUTATION, this::configureMutation);

    }

    private RuntimeWiring.Builder configureResolvers(RuntimeWiring.Builder builder) {
        for (GraphQLConfigurer resolver : Arrays.asList(
                basicResolver("CreateTenantPayload", "Tenant"),
                basicResolver("CreateAuthorityPayload", "Authority"),
                basicResolver("UpdateAuthorityPayload", "Authority"),
                basicResolver("AuthorityPayload", "Authority"),
                basicResolver("CreateOperatorPayload", "Operator"),
                basicResolver("UpdateOperatorPayload", "Operator"),
                basicResolver("OperatorPayload", "Operator"),
                basicResolver("OperatorsConnectionPayload", "OperatorsConnection"),
                basicResolver("CreateTransportModePayload", "TransportMode"),
                basicResolver("UpdateTransportModePayload", "TransportMode"),
                basicResolver("TransportModePayload", "TransportMode"),
                basicResolver("TransportModesConnectionPayload", "TransportModesConnection"),
                basicResolver("CreateVehiclePayload", "Vehicle"),
                basicResolver("UpdateVehiclePayload", "Vehicle"),
                basicResolver("VehiclePayload", "Vehicle"),
                basicResolver("VehiclesConnectionPayload", "VehiclesConnection"),
                basicResolver("SupportTypePayload", "SupportType"),
                basicResolver("SupportTypesConnectionPayload", "SupportTypesConnection"),
                basicResolver("CreateSupportTypePayload", "SupportType"),
                basicResolver("UpdateSupportTypePayload", "SupportType"),
                basicResolver("CreateVATPayload", "VAT"),
                basicResolver("UpdateVATPayload", "VAT"),
                basicResolver("VATPayload", "VAT"),
                basicResolver("VATsConnectionPayload", "VATsConnection"))) {
            builder = resolver.configure(builder);
        }
        return builder;
    }

    private static GraphQLConfigurer basicResolver(String payloadType, String dataType) {
        TypeResolver resolver = env -> {
            Object javaObject = env.getObject();
            if (javaObject.getClass().isAssignableFrom(ErrorGQL.class)) {
                return env.getSchema().getObjectType("Error");
            }
            return env.getSchema().getObjectType(dataType);
        };
        return builder -> builder.type(payloadType, tw -> tw.typeResolver(resolver));
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
                    Map<String, Object> input = env.getArgument("input");
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
