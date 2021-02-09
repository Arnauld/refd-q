package org.technbolts.busd.infra.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.mutiny.core.Vertx;
import org.jboss.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GraphQLController {
    private static final Logger LOG = Logger.getLogger(GraphQLController.class);

    private final Vertx vertx;
    private final Instance<GraphQLConfigurer> configurers;

    @Inject
    public GraphQLController(Vertx vertx, Instance<GraphQLConfigurer> configurers) {
        this.vertx = vertx;
        this.configurers = configurers;
    }

    /**
     * Declare `/graphql` route handler.
     */
    @SuppressWarnings("unused")
    public void init(@Observes Router router) {
        GraphQL graphQL = initGraphQL();
        GraphQLHandler graphQLHandler = GraphQLHandler.create(graphQL).queryContext(QueryContext::newQueryContext);

        router.route("/graphql").handler(graphQLHandler);
    }

    private GraphQL initGraphQL() {
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring()
                .directive("auth", new AuthDirective());
        RuntimeWiring runtimeWiring = configure(builder).build();

        TypeDefinitionRegistry typeDefinitionRegistry = typeDefinitionRegistry();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        return new GraphQL.Builder(graphQLSchema).build();
    }

    private RuntimeWiring.Builder configure(RuntimeWiring.Builder builder) {
        defaultConfigure(builder);
        for (GraphQLConfigurer configurer : configurers) {
            builder = configurer.configure(builder);
        }
        return builder;
    }

    protected void defaultConfigure(RuntimeWiring.Builder builder) {
        TypeResolver tHasAuditMeta = env -> {
            Object javaObject = env.getObject();
            String objectType = javaObject.getClass().getName().replace("GQL", "");
            return env.getSchema().getObjectType(objectType);
        };
        builder.type("HasAuditMeta", tw -> tw.typeResolver(tHasAuditMeta));

        TypeResolver tError = env -> {
            Object javaObject = env.getObject();
            String objectType = javaObject.getClass().getName().replace("GQL", "");
            return env.getSchema().getObjectType(objectType);
        };
        builder.type("Error", tw -> tw.typeResolver(tError));
    }

    protected TypeDefinitionRegistry typeDefinitionRegistry() {
        SchemaParser schemaParser = new SchemaParser();
        String schema = vertx.fileSystem().readFileBlocking("graphql/schema.graphql").toString();
        return schemaParser.parse(schema);
    }
}
