package org.technbolts.busd.infra.graphql.conf;

import graphql.schema.idl.RuntimeWiring;
import org.technbolts.busd.infra.graphql.GraphQLConfigurer;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;

import static org.technbolts.busd.infra.graphql.PayloadErrorResolver.payloadErrorResolver;

@ApplicationScoped
public class ConfGraphQLConfigurer implements GraphQLConfigurer {

    @Override
    public RuntimeWiring.Builder configure(RuntimeWiring.Builder builder) {
        return configureResolvers(builder);
    }

    private RuntimeWiring.Builder configureResolvers(RuntimeWiring.Builder builder) {
        for (GraphQLConfigurer resolver : Arrays.asList(
                payloadErrorResolver("CreateTransportModePayload", "TransportMode"),
                payloadErrorResolver("UpdateTransportModePayload", "TransportMode"),
                payloadErrorResolver("TransportModePayload", "TransportMode"),
                payloadErrorResolver("TransportModesConnectionPayload", "TransportModesConnection"),
                payloadErrorResolver("CreateVehiclePayload", "Vehicle"),
                payloadErrorResolver("UpdateVehiclePayload", "Vehicle"),
                payloadErrorResolver("VehiclePayload", "Vehicle"),
                payloadErrorResolver("VehiclesConnectionPayload", "VehiclesConnection"),
                payloadErrorResolver("SupportTypePayload", "SupportType"),
                payloadErrorResolver("SupportTypesConnectionPayload", "SupportTypesConnection"),
                payloadErrorResolver("CreateSupportTypePayload", "SupportType"),
                payloadErrorResolver("UpdateSupportTypePayload", "SupportType"),
                payloadErrorResolver("CreateVATPayload", "VAT"),
                payloadErrorResolver("UpdateVATPayload", "VAT"),
                payloadErrorResolver("VATPayload", "VAT"),
                payloadErrorResolver("VATsConnectionPayload", "VATsConnection"))) {
            builder = resolver.configure(builder);
        }
        return builder;
    }
}
