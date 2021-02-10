package org.technbolts.busd.infra.graphql.conf;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CreateOrganizationPayloadGQL {
    public final OrganizationGQL organization;
    public final ErrorGQL error;

    public CreateOrganizationPayloadGQL(OrganizationGQL organization, ErrorGQL error) {
        this.organization = organization;
        this.error = error;
    }
}
