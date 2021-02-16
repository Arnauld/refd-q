package org.technbolts.busd.infra.graphql.conf;

import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import org.technbolts.busd.infra.graphql.GraphQLConfigurer;
import org.technbolts.busd.infra.graphql.PayloadErrorResolver;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;

@ApplicationScoped
public class TenantGraphQLConfigurer implements GraphQLConfigurer {

    @Override
    public RuntimeWiring.Builder configure(RuntimeWiring.Builder builder) {
        builder = configureResolvers(builder);
        return builder
                .type(QUERY, this::configureQuery)
                .type(MUTATION, this::configureMutation);

    }

    private TypeRuntimeWiring.Builder configureMutation(TypeRuntimeWiring.Builder builder) {
        return builder;
    }

    private TypeRuntimeWiring.Builder configureQuery(TypeRuntimeWiring.Builder builder) {
        return builder;
    }

    private RuntimeWiring.Builder configureResolvers(RuntimeWiring.Builder builder) {
        for (GraphQLConfigurer resolver : Arrays.asList(
                PayloadErrorResolver.payloadErrorResolver("CreateTenantPayload", "Tenant"))) {
            builder = resolver.configure(builder);
        }
        return builder;
    }

}
