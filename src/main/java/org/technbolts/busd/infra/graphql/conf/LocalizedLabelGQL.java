package org.technbolts.busd.infra.graphql.conf;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class LocalizedLabelGQL {
    public String locale;
    public String label;

    public LocalizedLabelGQL(String locale, String label) {
        this.locale = locale;
        this.label = label;
    }

    public static List<LocalizedLabelGQL> listFromMap(List<Map<String, String>> label) {
        return label.stream()
                .map(LocalizedLabelGQL::fromMap)
                .collect(toList());
    }

    private static LocalizedLabelGQL fromMap(Map<String, String> m) {
        return new LocalizedLabelGQL(m.get("locale"), m.get("label"));
    }
}