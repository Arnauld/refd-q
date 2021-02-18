package org.technbolts.busd.infra.db;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jboss.logging.Logger;
import org.technbolts.busd.core.ExecutionContext;
import org.technbolts.busd.core.organizations.*;
import org.technbolts.busd.infra.graphql.QueryContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.ZoneOffset;

import static java.util.Arrays.asList;
import static org.technbolts.busd.core.ImageId.imageIdOrNull;
import static org.technbolts.busd.core.organizations.AuthorityId.authorityId;
import static org.technbolts.busd.core.organizations.OperatorId.operatorId;
import static org.technbolts.busd.infra.db.DbHelpers.toAddress;
import static org.technbolts.busd.infra.db.DbHelpers.toAddressPgType;
import static org.technbolts.busd.infra.db.DbHelpers.toInstant;
import static org.technbolts.busd.infra.db.DbHelpers.toJson;
import static org.technbolts.busd.infra.db.DbHelpers.toKeyValues;
import static org.technbolts.busd.infra.db.DbHelpers.toLocalizedLabel;

@ApplicationScoped
public class PgPoolOrganizations implements Organizations {

    private static final Logger LOG = Logger.getLogger(PgPoolOrganizations.class);
    private final ContextualizedPgPool pgPool;

    @Inject
    public PgPoolOrganizations(ContextualizedPgPool pgPool) {
        this.pgPool = pgPool;
    }

    @Override
    public Uni<Authority> authority(ExecutionContext context) {
        String sql = "select * from authorities where tenant_id = $1";
        Tuple args = Tuple.of(context.tenantId().raw());
        return pgPool.withConnectionUni(context, conn ->
                conn.preparedQuery(sql)
                        .execute(args)
                        .onItem().transformToUni(DbHelpers::singleResult)
                        .map(this::toAuthority)
        );
    }

    @Override
    public Uni<Authority> findAuthorityById(QueryContext context, AuthorityId id) {
        String sql = "select * from authorities where id = $1";
        Tuple args = Tuple.of(id.raw());
        return pgPool.withConnectionUni(context, conn ->
                conn.preparedQuery(sql)
                        .execute(args)
                        .onItem().transformToUni(DbHelpers::singleResult)
                        .map(this::toAuthority)
        );
    }

    @Override
    public Uni<AuthorityId> createAuthority(ExecutionContext context, NewAuthority newAuthority) {
        String sql = "" +
                "insert into authorities " +
                "(code, label, legal_name, postal_address, phone_number, web_site, contact_email, social_networks) " +
                "values ($1, $2, $3, $4, $5, $6, $7, $8) returning id";
        Tuple args = Tuple.tuple(asList(
                newAuthority.code(),
                toJson(newAuthority.label()),
                newAuthority.legalName(),
                newAuthority.postalAddress(),
                newAuthority.phoneNumber(),
                newAuthority.webSite(),
                newAuthority.contactEmail(),
                toJson(newAuthority.socialNetworks())));

        return pgPool.withConnectionUni(context, conn ->
                conn.preparedQuery(sql)
                        .execute(args)
                        .onItem().transformToUni(DbHelpers::singleResult)
                        .map(row -> toAuthorityId(row, "id"))
        );
    }

    @Override
    public Uni<Operator> findOperatorById(ExecutionContext context, OperatorId id) {
        String sql = "select * from operators where id = $1";
        Tuple args = Tuple.of(id.raw());
        return pgPool.withConnectionUni(context, conn ->
                conn.preparedQuery(sql)
                        .execute(args)
                        .onItem().transformToUni(DbHelpers::singleResult)
                        .map(this::toOperator)
        );
    }

    @Override
    public Uni<OperatorId> createOperator(ExecutionContext context, NewOperator newOperator) {
        String sql = "" +
                "insert into operators " +
                "(authority_id, " +
                " parent_id, " +
                " code, " +
                " deactivation_date, " +
                " label, " +
                " legal_name, " +
                " capital_amount, " +
                " registration_number, " +
                " vat_number, " +
                " head_office_address, " +
                " postal_address, " +
                " phone_number, " +
                " web_site, " +
                " contact_email, " +
                " social_networks) " +
                " values ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15)" +
                " returning id";
        Tuple args = Tuple.tuple(asList(
                newOperator.authorityId().raw(),
                newOperator.parentId().map(OperatorId::raw).orElse(null),
                newOperator.code(),
                newOperator.deactivationDate().map(d -> d.atOffset(ZoneOffset.UTC)).orElse(null),
                toJson(newOperator.label()),
                newOperator.legalName(),
                newOperator.capitalAmount(),
                newOperator.registrationNumber(),
                newOperator.vatNumber(),
                toAddressPgType(newOperator.headOfficeAddress()),
                toAddressPgType(newOperator.postalAddress()),
                newOperator.phoneNumber(),
                newOperator.webSite(),
                newOperator.contactEmail(),
                toJson(newOperator.socialNetworks())));

        return pgPool.withConnectionUni(context, conn ->
                conn.preparedQuery(sql)
                        .execute(args)
                        .onItem().transformToUni(DbHelpers::singleResult)
                        .map(row -> toOperatorId(row, "id"))
        );
    }

    private AuthorityId toAuthorityId(Row row, String columnName) {
        return authorityId(row.getInteger(columnName));
    }

    private OperatorId toOperatorId(Row row, String columnName) {
        return operatorId(row.getInteger(columnName));
    }

    private Authority toAuthority(Row row) {
        return new Authority(
                toAuthorityId(row, "id"),
                row.getString("code"),
                toLocalizedLabel(row, "label"),
                row.getString("legal_name"),
                imageIdOrNull(row.getInteger("logo_id")),
                toAddress(row, "postal_address"),
                row.getString("phone_number"),
                row.getString("web_site"),
                row.getString("contact_email"),
                toKeyValues(row, "social_networks")
        );
    }

    private Operator toOperator(Row row) {
        return new Operator(
                toOperatorId(row, "id"),
                toAuthorityId(row, "authority_id"),
                row.getString("code"),
                toInstant(row.getOffsetDateTime("deactivation_date")),
                toLocalizedLabel(row, "label"),
                row.getString("legal_name"),
                imageIdOrNull(row.getInteger("logo_id")),
                row.getString("capital_amount"),
                row.getString("registration_number"),
                row.getString("vat_number"),
                toAddress(row, "head_office_address"),
                toAddress(row, "postal_address"),
                row.getString("phone_number"),
                row.getString("web_site"),
                row.getString("contact_email"),
                toKeyValues(row, "social_networks")
        );
    }

}
