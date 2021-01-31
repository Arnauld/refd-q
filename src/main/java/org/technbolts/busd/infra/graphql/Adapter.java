package org.technbolts.busd.infra.graphql;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Adapter<T> {
    private final T adapted;

    public Adapter(T adapted) {
        this.adapted = adapted;
    }

    public T adapted() {
        return adapted;
    }
}
