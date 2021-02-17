package org.technbolts.busd.infra.db;

import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.RowSet;
import org.jboss.logging.Logger;
import org.technbolts.busd.core.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

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
        Caller createdBy = Caller.caller(row.getString("created_by"), Caller.Type.valueOf(row.getString("created_by_type")));
        Caller updatedBy = Caller.caller(row.getString("updated_by"), Caller.Type.valueOf(row.getString("updated_by_type")));
        return new AuditMeta(
                toInstant(row.getOffsetDateTime("created_at")),
                createdBy,
                toInstant(row.getOffsetDateTime("updated_at")),
                updatedBy);
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

    public static JsonObject toJson(LocalizedLabel label) {
        return new JsonObject(label.asMapOfObject());
    }

    public static JsonObject toJson(KeyValues kvs) {
        return new JsonObject(kvs.asMapOfObject());
    }

}
