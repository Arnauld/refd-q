package org.technbolts.busd.infra.graphql;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.mutiny.core.Vertx;
import org.jboss.logging.Logger;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
        try {
            final String input = loadSchema().subscribeAsCompletionStage().get(200, TimeUnit.MILLISECONDS);
            return new SchemaParser().parse(input);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("Failed to load schema", e);
        }
    }

    private Uni<String> loadSchema() {
        return vertx.fileSystem()
                .readDir("graphql/", "[a-zA-Z0-9_-]+\\.graphql$")
                .onItem().transformToMulti(files -> Multi.createFrom().iterable(files))
                .map(filepath -> Tuple2.of(filepath, vertx.fileSystem().readFileBlocking(filepath).toString()))
                .collectItems()
                .asList()
                .map(tuples -> {
                    List<Tuple2<String, String>> copy = new ArrayList<>(tuples);
                    Collections.sort(copy, Comparator.comparing(Tuple2::getItem1));
                    StringBuilder allInOne = new StringBuilder();
                    for (Tuple2<String, String> t2 : copy) {
                        allInOne.append(t2.getItem2()).append('\n');
                    }
                    return allInOne.toString();
                });
    }
}
