package org.technbolts.busd.infra.graphql.conf;

import graphql.schema.DataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import org.technbolts.busd.core.Address;
import org.technbolts.busd.core.KeyValues;
import org.technbolts.busd.core.LocalizedLabel;
import org.technbolts.busd.core.organizations.Authority;
import org.technbolts.busd.core.organizations.NewAuthority;
import org.technbolts.busd.core.organizations.Operator;
import org.technbolts.busd.core.organizations.Organizations;
import org.technbolts.busd.infra.graphql.GraphQLConfigurer;
import org.technbolts.busd.infra.graphql.QueryContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.technbolts.busd.core.organizations.OperatorId.operatorId;
import static org.technbolts.busd.infra.graphql.PayloadErrorResolver.payloadErrorResolver;

@ApplicationScoped
public class OrganizationGraphQLConfigurer implements GraphQLConfigurer {

    private final Organizations organizations;

    @Inject
    public OrganizationGraphQLConfigurer(Organizations organizations) {
        this.organizations = organizations;
    }

    @Override
    public RuntimeWiring.Builder configure(RuntimeWiring.Builder builder) {
        builder = configureResolvers(builder);
        return builder
                .type(QUERY, this::configureQuery)
                .type(MUTATION, this::configureMutation);
    }

    private RuntimeWiring.Builder configureResolvers(RuntimeWiring.Builder builder) {
        for (GraphQLConfigurer resolver : Arrays.asList(
                payloadErrorResolver("CreateAuthorityPayload", "Authority"),
                payloadErrorResolver("UpdateAuthorityPayload", "Authority"),
                payloadErrorResolver("AuthorityPayload", "Authority"),
                payloadErrorResolver("CreateOperatorPayload", "Operator"),
                payloadErrorResolver("UpdateOperatorPayload", "Operator"),
                payloadErrorResolver("OperatorPayload", "Operator"),
                payloadErrorResolver("OperatorsConnectionPayload", "OperatorsConnection"))) {
            builder = resolver.configure(builder);
        }
        return builder;
    }

    public TypeRuntimeWiring.Builder configureQuery(TypeRuntimeWiring.Builder builder) {
        return builder
                .dataFetcher("authority", (DataFetcher<CompletableFuture<Object>>) env -> {
                    QueryContext context = env.getContext();
                    return organizations.authority(context)
                            .map(this::toAuthorityGQL)
                            .map(o -> (Object) o)
                            .subscribeAsCompletionStage();
                })
                .dataFetcher("operatorById", (DataFetcher<CompletableFuture<Object>>) env -> {
                    int id = env.getArgument("id");
                    QueryContext context = env.getContext();
                    return organizations.findOperatorById(context, operatorId(id))
                            .map(this::toOperatorGQL)
                            .map(o -> (Object) o)
                            .subscribeAsCompletionStage();
                });
    }

    public TypeRuntimeWiring.Builder configureMutation(TypeRuntimeWiring.Builder builder) {
        return builder
                .dataFetcher("createAuthority", (DataFetcher<CompletableFuture<Object>>) env -> {
                    Map<String, Object> input = env.getArgument("input");
                    NewAuthority newAuthority = toNewAuthority(input);
                    QueryContext context = env.getContext();
                    return organizations.createAuthority(context, newAuthority)
                            .flatMap(id -> organizations.findAuthorityById(context, id))
                            .map(this::toAuthorityGQL)
                            .map(o -> (Object) o)
                            .subscribeAsCompletionStage();
                });
    }

    private NewAuthority toNewAuthority(Map<String, Object> input) {
        String code = (String) input.get("code");
        String legalName = (String) input.get("legalName");
        return new NewAuthority(
                code,
                toLocalizedLabel((List<Map<String, String>>) input.get("label")),
                legalName,
                toAddress((Map<String, Object>) input.get("postalAddress")),
                (String) input.get("phoneNumber"),
                (String) input.get("webSite"),
                (String) input.get("contactEmail"),
                toKeyValues((List<Map<String, String>>) input.get("socialNetworks"))
        );
    }

    private KeyValues toKeyValues(List<Map<String, String>> mapList) {
        if (mapList == null)
            return null;

        Map<String, String> raw = new HashMap<>();
        for (Map<String, String> e : mapList) {
            raw.put(e.get("key"), e.get("value"));
        }
        return new KeyValues(raw);
    }

    private Address toAddress(Map<String, Object> objectMap) {
        if (objectMap == null)
            return null;
        return new Address(
                (String) objectMap.get("address1"),
                (String) objectMap.get("address2"),
                (String) objectMap.get("city"),
                (String) objectMap.get("zipcode"),
                (String) objectMap.get("country"));
    }

    private LocalizedLabel toLocalizedLabel(List<Map<String, String>> mapList) {
        if (mapList == null)
            return null;

        Map<String, String> raw = new HashMap<>();
        for (Map<String, String> e : mapList) {
            raw.put(e.get("locale"), e.get("mapList"));
        }
        return new LocalizedLabel(raw);
    }

    private AuthorityGQL toAuthorityGQL(Authority oql) {
        return new AuthorityGQL();
    }

    private OperatorGQL toOperatorGQL(Operator oql) {
        return null;
    }

}
