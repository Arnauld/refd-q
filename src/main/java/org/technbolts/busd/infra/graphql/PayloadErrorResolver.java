package org.technbolts.busd.infra.graphql;

import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import org.jboss.logging.Logger;
import org.technbolts.busd.infra.graphql.conf.ErrorGQL;

public class PayloadErrorResolver implements GraphQLConfigurer {
    private static final Logger LOG = Logger.getLogger(PayloadErrorResolver.class);

    private final String payloadType;
    private final String dataType;

    public PayloadErrorResolver(String payloadType, String dataType) {
        this.payloadType = payloadType;
        this.dataType = dataType;
    }

    public static GraphQLConfigurer payloadErrorResolver(String payloadType, String dataType) {
        return new PayloadErrorResolver(payloadType, dataType);
    }

    @Override
    public RuntimeWiring.Builder configure(RuntimeWiring.Builder builder) {
        TypeResolver resolver = env -> {
            Object javaObject = env.getObject();
            LOG.infof("Resolving (%s,%s) based on %s", payloadType, dataType, javaObject);
            if (javaObject.getClass().isAssignableFrom(ErrorGQL.class)) {
                return env.getSchema().getObjectType("Error");
            }
            return env.getSchema().getObjectType(dataType);
        };
        return builder.type(payloadType, tw -> tw.typeResolver(resolver));
    }
}
