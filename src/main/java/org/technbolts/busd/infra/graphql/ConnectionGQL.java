package org.technbolts.busd.infra.graphql;

import java.util.ArrayList;
import java.util.List;

public class ConnectionGQL<R> {
    public static class EdgeGQL<R> {
        public final R node;

        public EdgeGQL(R node) {
            this.node = node;
        }
    }

    public final List<EdgeGQL<R>> edges = new ArrayList<>();

    public void append(R data) {
        edges.add(new EdgeGQL<>(data));
    }
}
