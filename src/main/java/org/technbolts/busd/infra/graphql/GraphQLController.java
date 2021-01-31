package org.technbolts.busd.infra.graphql;

import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.mutiny.core.Vertx;
import org.jboss.logging.Logger;
import org.technbolts.busd.core.tenants.Tenant;
import org.technbolts.busd.core.tenants.Tenants;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GraphQLController {
    private static final Logger LOG = Logger.getLogger(GraphQLController.class);

    private final Vertx vertx;
    private final Tenants tenants;

    @Inject
    public GraphQLController(Vertx vertx, Tenants tenants) {
        this.vertx = vertx;
        this.tenants = tenants;
    }

    public void init(@Observes Router router) {
        GraphQL graphQL = setupGraphQL();
        GraphQLHandler graphQLHandler = GraphQLHandler.create(graphQL).queryContext(QueryContext::newQueryContext);

        router.route("/graphql").handler(rc -> {
            System.out.println("GraphQLController.init(" + rc.pathParams() + ")");
            graphQLHandler.handle(rc);
        });
    }

    private GraphQL setupGraphQL() {
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder.dataFetcher("tenants", (DataFetcher<CompletableFuture<List<TenantGQL>>>) env -> {
                    String glob = env.getArgument("glob");
                    QueryContext context = env.getContext();
                    return tenants.list(context)
                            .map(this::toGraphQL)
                            .collectItems().asList().subscribe().asCompletionStage();
                }))
                //.type("Mutation", builder -> builder.dataFetcher("addGroup", taskRepo::addGroup))
                .build();

        TypeDefinitionRegistry typeDefinitionRegistry = typeDefinitionRegistry();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

        return new GraphQL.Builder(graphQLSchema).build();
    }

    protected TypeDefinitionRegistry typeDefinitionRegistry() {
        SchemaParser schemaParser = new SchemaParser();
        String schema = vertx.fileSystem().readFileBlocking("graphql/schema.graphql").toString();
        return schemaParser.parse(schema);
    }

    private TenantGQL toGraphQL(Tenant tenant) {
        return new TenantGQL(tenant);
    }
}
