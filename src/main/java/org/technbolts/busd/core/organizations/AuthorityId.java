package org.technbolts.busd.core.organizations;

public class AuthorityId {

    public static AuthorityId authorityId(int raw) {
        if (raw < 0)
            throw new IllegalArgumentException("Cannot be negative");
        return new AuthorityId(raw);
    }

    private final int raw;

    private AuthorityId(int raw) {
        this.raw = raw;
    }

    @Override
    public String toString() {
        return "AuthorityId{" + raw + '}';
    }

    public int raw() {
        return raw;
    }

    public String asString() {
        return String.valueOf(raw);
    }}
