package org.technbolts.busd.infra.graphql;

import org.technbolts.busd.core.ErrorCode;
import org.technbolts.busd.infra.graphql.conf.PropertyGQL;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ErrorGQL {
    public final ErrorCode code;
    public final String message;
    public final List<PropertyGQL> args;

    public ErrorGQL(ErrorCode code, String message) {
        this(code, message, Collections.emptyList());
    }

    public ErrorGQL(ErrorCode code, String message, List<PropertyGQL> args) {
        this.code = code;
        this.message = message;
        this.args = args;
    }

    @Override
    public String toString() {
        return "ErrorGQL{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", args=" + args +
                '}';
    }
}
