package org.technbolts.busd.core.tenants;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class NewTenant {
    private final Integer id;
    private final String code;
    private final String name;

    public NewTenant(Integer id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Integer id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String code() {
        return code;
    }
}
