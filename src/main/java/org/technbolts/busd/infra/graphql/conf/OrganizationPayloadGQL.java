package org.technbolts.busd.infra.graphql.conf;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class OrganizationPayloadGQL {
    public final OrganizationGQL organization;
    public final ErrorGQL error;

    public OrganizationPayloadGQL(OrganizationGQL organization, ErrorGQL error) {
        this.organization = organization;
        this.error = error;
    }
}
