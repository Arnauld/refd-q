package org.technbolts.busd.core.organizations;

import org.technbolts.busd.core.Address;
import org.technbolts.busd.core.ImageId;
import org.technbolts.busd.core.KeyValues;
import org.technbolts.busd.core.LocalizedLabel;

import java.time.Instant;

public class Operator {
    private final OperatorId id;
    private final AuthorityId authorityId;
    private final String code;
    private final Instant deactivationDate;
    private final LocalizedLabel label;
    private final String legalName;
    private final ImageId logoId;
    private final String capitalAmount;
    private final String registrationNumber;
    private final String vatNumber;
    private final Address headOfficeAddress;
    private final Address postalAddress;
    private final String phoneNumber;
    private final String webSite;
    private final String contactEmail;
    private final KeyValues socialNetworks;

    public Operator(OperatorId id,
                    AuthorityId authorityId,
                    String code,
                    Instant deactivationDate,
                    LocalizedLabel label,
                    String legalName,
                    ImageId logoId,
                    String capitalAmount,
                    String registrationNumber,
                    String vatNumber,
                    Address headOfficeAddress,
                    Address postalAddress,
                    String phoneNumber,
                    String webSite,
                    String contactEmail,
                    KeyValues socialNetworks) {

        this.id = id;
        this.authorityId = authorityId;
        this.code = code;
        this.deactivationDate = deactivationDate;
        this.label = label;
        this.legalName = legalName;
        this.logoId = logoId;
        this.capitalAmount = capitalAmount;
        this.registrationNumber = registrationNumber;
        this.vatNumber = vatNumber;
        this.headOfficeAddress = headOfficeAddress;
        this.postalAddress = postalAddress;
        this.phoneNumber = phoneNumber;
        this.webSite = webSite;
        this.contactEmail = contactEmail;
        this.socialNetworks = socialNetworks;
    }
}
