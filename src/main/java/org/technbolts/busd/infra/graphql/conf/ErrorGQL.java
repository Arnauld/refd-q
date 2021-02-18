package org.technbolts.busd.infra.graphql.conf;

import org.technbolts.busd.core.ErrorCode;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ErrorGQL {
    public final ErrorCode code;
    public final String message;
    public final List<PropertyGQL> args;

    public ErrorGQL(ErrorCode code, String message, List<PropertyGQL> args) {
        this.code = code;
        this.message = message;
        this.args = args;
    }
}
