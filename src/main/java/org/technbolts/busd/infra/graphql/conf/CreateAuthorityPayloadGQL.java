package org.technbolts.busd.infra.graphql.conf;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CreateAuthorityPayloadGQL {
    public final OrganizationGQL organization;
    public final ErrorGQL error;

    public CreateAuthorityPayloadGQL(OrganizationGQL organization, ErrorGQL error) {
        this.organization = organization;
        this.error = error;
    }
}
