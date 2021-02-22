package org.technbolts.busd.core.organizations;

import org.technbolts.busd.core.Address;
import org.technbolts.busd.core.KeyValues;
import org.technbolts.busd.core.LocalizedLabel;

import java.time.Instant;
import java.util.Optional;

public class NewOperator {
    private final AuthorityId authorityId;
    private final OperatorId parentId;
    private final String code;
    private final Instant deactivationDate;
    private final LocalizedLabel label;
    private final String legalName;
    private final String capitalAmount;
    private final String registrationNumber;
    private final String vatNumber;
    private final Address headOfficeAddress;
    private final Address postalAddress;
    private final String phoneNumber;
    private final String webSite;
    private final String contactEmail;
    private final KeyValues socialNetworks;

    public NewOperator(AuthorityId authorityId,
                       OperatorId parentId,
                       String code,
                       Instant deactivationDate,
                       LocalizedLabel label,
                       String legalName,
                       String capitalAmount,
                       String registrationNumber,
                       String vatNumber,
                       Address headOfficeAddress,
                       Address postalAddress,
                       String phoneNumber,
                       String webSite,
                       String contactEmail,
                       KeyValues socialNetworks) {
        this.authorityId = authorityId;
        this.parentId = parentId;
        this.code = code;
        this.deactivationDate = deactivationDate;
        this.label = label;
        this.legalName = legalName;
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

    public AuthorityId authorityId() {
        return authorityId;
    }

    public Optional<OperatorId> parentId() {
        return Optional.ofNullable(parentId);
    }

    public String code() {
        return code;
    }

    public Optional<Instant> deactivationDate() {
        return Optional.ofNullable(deactivationDate);
    }

    public LocalizedLabel label() {
        return label;
    }

    public String legalName() {
        return legalName;
    }

    public String capitalAmount() {
        return capitalAmount;
    }

    public String registrationNumber() {
        return registrationNumber;
    }

    public String vatNumber() {
        return vatNumber;
    }

    public Address headOfficeAddress() {
        return headOfficeAddress;
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
