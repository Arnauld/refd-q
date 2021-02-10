package org.technbolts.busd.infra.graphql.conf;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ErrorGQL {
    public final String code;
    public final List<PropertyGQL> args;
    public final String message;

    public ErrorGQL(String code, String message, PropertyGQL... args) {
        this.code = code;
        this.args = Arrays.asList(args);
        this.message = message;
    }

    public static ErrorGQL notFound() {
        return new ErrorGQL("not-found", "Not found");
    }
}
