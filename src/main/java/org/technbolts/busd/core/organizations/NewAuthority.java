package org.technbolts.busd.core.organizations;

import org.technbolts.busd.core.Address;
import org.technbolts.busd.core.KeyValues;
import org.technbolts.busd.core.LocalizedLabel;

public class NewAuthority {
    private final String code;
    private final LocalizedLabel label;
    private final String legalName;
    private final Address postalAddress;
    private final String phoneNumber;
    private final String webSite;
    private final String contactEmail;
    private final KeyValues socialNetworks;

    public NewAuthority(String code,
                        LocalizedLabel label,
                        String legalName,
                        Address postalAddress,
                        String phoneNumber,
                        String webSite,
                        String contactEmail,
                        KeyValues socialNetworks) {
        this.code = code;
        this.label = label;
        this.legalName = legalName;
        this.postalAddress = postalAddress;
        this.phoneNumber = phoneNumber;
        this.webSite = webSite;
        this.contactEmail = contactEmail;
        this.socialNetworks = socialNetworks;
    }

    public String code() {
        return code;
    }

    public LocalizedLabel label() {
        return label;
    }

    public String legalName() {
        return legalName;
    }

    public Address postalAddress() {
        return postalAddress;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public String webSite() {
        return webSite;
    }

    public String contactEmail() {
        return contactEmail;
    }

    public KeyValues socialNetworks() {
        return socialNetworks;
    }
}
