package org.technbolts.busd.infra.graphql.conf;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CreateOrganizationInputGQL {

    public String code;
    public List<LocalizedLabelGQL> label;
    public String legalName;
    public AddressInputGQL postalAddress;
    public String phoneNumber;
    public String webSite;
    public String contactEmail;
    public List<PropertyInputGQL> socialNetworks;

    public static CreateOrganizationInputGQL fromMap(Map<String, Object> input) {
        CreateOrganizationInputGQL inputGQL = new CreateOrganizationInputGQL();
        inputGQL.code = (String)input.get("code");
        inputGQL.label = LocalizedLabelGQL.listFromMap((List<Map<String,String>>) input.get("label"));
        inputGQL.legalName = (String)input.get("legalName");
        return inputGQL;
    }
}
