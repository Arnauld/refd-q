package org.technbolts.busd.core;

public class Address {
    private final String address1;
    private final String address2;
    private final String city;
    private final String zipcode;
    private final String country;

    public Address(String address1, String address2, String city, String zipcode, String country) {
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.zipcode = zipcode;
        this.country = country;
    }

    public String address1() {
        return address1;
    }

    public String address2() {
        return address2;
    }

    public String city() {
        return city;
    }

    public String zipcode() {
        return zipcode;
    }

    public String country() {
        return country;
    }
}
