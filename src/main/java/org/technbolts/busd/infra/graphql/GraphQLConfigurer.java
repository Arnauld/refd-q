package org.technbolts.busd.infra.graphql;

import graphql.schema.idl.RuntimeWiring;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface GraphQLConfigurer {
    String QUERY = "Query";
    String MUTATION = "Mutation";

    RuntimeWiring.Builder configure(RuntimeWiring.Builder builder);
}
