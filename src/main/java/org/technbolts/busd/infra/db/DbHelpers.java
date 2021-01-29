package org.technbolts.busd.infra.db;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.RowSet;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class DbHelpers {

    public static Uni<? extends Row> singleResult(RowSet<Row> set) {
        RowIterator<Row> it = set.iterator();
        if (it.hasNext())
            return Uni.createFrom().item(it.next());
        return Uni.createFrom().nothing();
    }
}
