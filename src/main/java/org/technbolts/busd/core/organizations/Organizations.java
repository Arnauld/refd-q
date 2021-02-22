package org.technbolts.busd.core.organizations;

import io.smallrye.mutiny.Uni;
import org.technbolts.busd.core.ExecutionContext;
import org.technbolts.busd.infra.graphql.QueryContext;

public interface Organizations {
    Uni<Authority> authority(ExecutionContext context);
    Uni<Authority> findAuthorityById(QueryContext context, AuthorityId id);
    Uni<AuthorityId> createAuthority(ExecutionContext context, NewAuthority newAuthority);


    Uni<Operator> findOperatorById(ExecutionContext context, OperatorId operatorId);
    Uni<OperatorId> createOperator(ExecutionContext context, NewOperator newOperator);

}
