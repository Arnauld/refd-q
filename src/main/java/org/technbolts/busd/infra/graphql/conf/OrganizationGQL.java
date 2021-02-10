package org.technbolts.busd.infra.graphql.conf;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class OrganizationGQL {
    public int id;
    public String code;
    public List<LocalizedLabelGQL> label;
    public String legalName;
    public ImageMetaGQL logo;
    public AddressGQL postalAddress;
    public String phoneNumber;
    public String webSite;
    public String contactEmail;
    public List<PropertyGQL> socialNetworks;
}
