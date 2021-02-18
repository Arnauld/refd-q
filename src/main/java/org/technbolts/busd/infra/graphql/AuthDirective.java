package org.technbolts.busd.infra.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactories;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.jboss.logging.Logger;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class AuthDirective implements SchemaDirectiveWiring {

    private static final Logger LOG = Logger.getLogger(AuthDirective.class);

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> env) {

        GraphQLFieldDefinition field = env.getElement();
        GraphQLFieldsContainer parentType = env.getFieldsContainer();

        DataFetcher<?> originalFetcher = env.getCodeRegistry().getDataFetcher(parentType, field);
        String permission = (String) env.getDirective().getArgument("permission").getValue();

        // build a data fetcher that first checks authorisation roles before then calling the original data fetcher
        DataFetcher<?> authDataFetcher = DataFetcherFactories.wrapDataFetcher(originalFetcher, (dataFetchingEnvironment, value) -> {
            QueryContext context = dataFetchingEnvironment.getContext();
            final boolean hasPermission = context.hasPermission(permission);
            LOG.infof("Checking auth(%s) on field '%s': %s", permission, field.getName(), hasPermission);
            if (hasPermission) {
                return value;
            } else {
                return null;
            }
        });
        //
        // now change the field definition to use the new authorising data fetcher
        env.getCodeRegistry().dataFetcher(parentType, field, authDataFetcher);
        return field;
    }
}