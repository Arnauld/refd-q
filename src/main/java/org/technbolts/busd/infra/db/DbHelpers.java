package org.technbolts.busd.infra.db;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jboss.logging.Logger;
import org.technbolts.busd.core.Address;
import org.technbolts.busd.core.AuditMeta;
import org.technbolts.busd.core.Caller;
import org.technbolts.busd.core.KeyValues;
import org.technbolts.busd.core.LocalizedLabel;
import org.technbolts.busd.core.tenants.Tenant;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class DbHelpers {

    private static final Logger LOG = Logger.getLogger(DbHelpers.class);

    public static Uni<? extends Row> singleResult(RowSet<Row> set) {
        RowIterator<Row> it = set.iterator();
        if (it.hasNext())
            return Uni.createFrom().item(it.next());
        return Uni.createFrom().nothing();
    }

    public static Instant toInstant(OffsetDateTime dateTime) {
        if (dateTime == null)
            return null;
        return dateTime.toInstant();
    }

    public static AuditMeta toAuditMeta(Row row) {
        return new AuditMeta(
                toInstant(row, "created_at"),
                caller(row, "created_by", "created_by_type"),
                toInstant(row, "updated_at"),
                caller(row, "updated_by", "updated_by_type"));
    }

    private static Instant toInstant(Row row, String columnName) {
        return toInstant(row.getOffsetDateTime(columnName));
    }

    private static Caller caller(Row row, String created_by, String created_by_type) {
        return Caller.caller(row.getString(created_by), Caller.Type.valueOf(row.getString(created_by_type)));
    }

    public static String toAddressPgType(Address address) {
        if (address == null)
            return null;
        return "("
                + pgQuote(address.address1()) + ","
                + pgQuote(address.address2()) + ","
                + pgQuote(address.city()) + ","
                + pgQuote(address.zipcode()) + ","
                + pgQuote(address.country()) + ")";
    }

    private static String pgQuote(String s) {
        return "'" + s.replace("'", "''") + "'";
    }

    public static Address toAddress(Row row, String columnName) {
        String raw = row.getString(columnName);
        LOG.warnf("Unable to parse address format >>%s<<", raw);
        return new Address(null, null, null, null, null);
    }

    public static LocalizedLabel toLocalizedLabel(Row row, String columnName) {
        Map<String, String> m = toMapOfStringString(row, columnName);
        return new LocalizedLabel(m);
    }

    public static KeyValues toKeyValues(Row row, String columnName) {
        Map<String, String> m = toMapOfStringString(row, columnName);
        return new KeyValues(m);
    }

    private static Map<String, String> toMapOfStringString(Row row, String columnName) {
        final int columnIndex = row.getColumnIndex(columnName);
        JsonObject json = row.get(JsonObject.class, columnIndex);

        Map<String, String> m = new HashMap<>();
        if (json != null) {
            for (String fieldName : json.fieldNames()) {
                m.put(fieldName, json.getString(fieldName));
            }
        }
        return m;
    }

    public static Object toJson(LocalizedLabel label) {
        if (label == null)
            return Tuple.JSON_NULL;
        return new JsonObject(label.asMapOfObject());
    }

    public static Object toJson(KeyValues kvs) {
        if (kvs == null)
            return Tuple.JSON_NULL;
        return new JsonObject(kvs.asMapOfObject());
    }

    public static <T, R> List<T> toList(RowSet<R> rs, Function<R, T> func) {
        List<T> ts = new ArrayList<>();
        for (R r : rs) {
            ts.add(func.apply(r));
        }
        return ts;
    }

    public static <T, R> Function<RowSet<T>, List<R>> rowSetToListUsing(Function<T, R> mapper) {
        return rs -> DbHelpers.toList(rs, mapper);
    }

    public static <T, R> Function<List<T>, List<R>> listUsing(Function<T, R> mapper) {
        return ls -> ls.stream().map(mapper).collect(Collectors.toList());
    }
}
