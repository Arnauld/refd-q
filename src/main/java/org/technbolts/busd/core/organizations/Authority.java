package org.technbolts.busd.core.organizations;

import org.technbolts.busd.core.Address;
import org.technbolts.busd.core.ImageId;
import org.technbolts.busd.core.KeyValues;
import org.technbolts.busd.core.LocalizedLabel;

public class Authority {
    private final AuthorityId id;
    private final String code;
    private final LocalizedLabel label;
    private final String legalName;
    private final ImageId logoId;
    private final Address postalAddress;
    private final String phoneNumber;
    private final String webSite;
    private final String contactEmail;
    private final KeyValues socialNetworks;

    public Authority(AuthorityId id,
                     String code,
                     LocalizedLabel label,
                     String legalName,
                     ImageId logoId,
                     Address postalAddress,
                     String phoneNumber,
                     String webSite,
                     String contactEmail,
                     KeyValues socialNetworks) {
        this.id = id;
        this.code = code;
        this.label = label;
        this.legalName = legalName;
        this.logoId = logoId;
        this.postalAddress = postalAddress;
        this.phoneNumber = phoneNumber;
        this.webSite = webSite;
        this.contactEmail = contactEmail;
        this.socialNetworks = socialNetworks;
    }

    public AuthorityId id() {
        return id;
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

    public ImageId logoId() {
        return logoId;
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
