package org.technbolts.busd.infra.graphql.conf;

import org.technbolts.busd.core.organizations.Authority;
import org.technbolts.busd.infra.graphql.Adapter;

public class AuthorityGQL extends Adapter<Authority> {
    public final int id;
    public final String code;
    public final String timezone;

    public AuthorityGQL(Authority authority) {
        super(authority);
        this.id = authority.id().raw();
        this.code = authority.code();
        this.timezone = authority.timezone();
    }

    @Override
    public String toString() {
        return "AuthorityGQL{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", tz='" + timezone + '\'' +
                '}';
    }
}
