package org.technbolts.busd.core.tenants;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class NewTenant {
    private final Integer id;
    private final String name;
    private final String code;

    public NewTenant(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
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
