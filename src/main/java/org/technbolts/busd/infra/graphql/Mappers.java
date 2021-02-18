package org.technbolts.busd.infra.graphql;

import org.technbolts.busd.core.ErrorCode;
import org.technbolts.busd.core.RefdException;
import org.technbolts.busd.infra.graphql.conf.ErrorGQL;
import org.technbolts.busd.infra.graphql.conf.PropertyGQL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyList;

public class Mappers {
    public static <R, T> Function<List<R>, ConnectionGQL<T>> toConnectionGQLUsing(Function<R, T> mapper) {
        return ls -> {
            ConnectionGQL<T> connectionGQL = new ConnectionGQL<>();
            for (R r : ls) {
                connectionGQL.append(mapper.apply(r));
            }
            return connectionGQL;
        };
    }

    public static ErrorGQL toError(Throwable throwable) {
        if (throwable instanceof RefdException) {
            RefdException e = (RefdException) throwable;
            return new ErrorGQL(e.errorCode(), e.message(), toPropertiesGQL(e.args()));
        }
        return new ErrorGQL(ErrorCode.SERVER_ERROR, throwable.getMessage(), emptyList());
    }

    public static List<PropertyGQL> toPropertiesGQL(Map<String, String> args) {
        List<PropertyGQL> ps = new ArrayList<>();
        for (Map.Entry<String, String> e : args.entrySet()) {
            ps.add(new PropertyGQL(e.getKey(), e.getValue()));
        }
        return ps;
    }
}
