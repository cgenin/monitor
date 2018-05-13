package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import org.dizitart.no2.*;
import org.dizitart.no2.filters.BaseFilter;
import org.dizitart.no2.store.NitriteMap;
import rx.Observable;
import rx.Single;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.dizitart.no2.filters.Filters.eq;

public class ImportExport extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ImportExport.class);

    public static final String IMPORT = ImportExport.class.getName() + ".import";
    public static final String EXPORT = ImportExport.class.getName() + ".export";

    @Override
    public void start() throws Exception {
        vertx.eventBus().<JsonObject>consumer(IMPORT, (msg) -> {
            if (NitriteDbs.instance.importFrom(msg.body())) {
                msg.reply(new JsonObject());
            } else {
                msg.fail(500, "Error in importing");
            }
        });

        vertx.eventBus().<JsonObject>consumer(EXPORT, (msg) -> {
            final NitriteCollection collection = NitriteDbs.instance
                    .getCollection(Schemas.RAW_COLLECTION);
            List<Document> documents = collection
                    .find(eq(Schemas.RAW_STATE, Treatments.END.getState()))
                    .toList();
            Stream<JsonObject> obs = documents.stream()
                    .map(NitriteDbs.Raws::toJson)
                    .map(json -> json.put("state", Treatments.END.getState()).put("ARCHIVE", true));
            if (documents.isEmpty()) {
                msg.reply(new JsonObject()
                        .put("numberOfExported", 0)
                        .put("numberOfDeleted", 0));
            } else {
                exportEvents(obs)
                        .subscribe(
                                nb -> {
                                    Set<NitriteId> ids = documents.stream()
                                            .map(Document::getId)
                                            .collect(Collectors.toSet());
                                    WriteResult remove = collection.remove(new RemoveByIdNititeFilter(ids));
                                    msg.reply(new JsonObject()
                                            .put("numberOfExported", nb)
                                            .put("numberOfDeleted", remove.getAffectedCount()));
                                },
                                err -> {
                                    logger.error("Error in exporting to MysqlPort", err);
                                    msg.fail(500, "Error in exporting to MysqlPort");
                                }
                        );

            }
        });
    }

    public Single<Integer> exportEvents(Stream<JsonObject> jsons) {
        Mysqls mysqls = Mysqls.Instance.get();
        return mysqls.connection()
                .flatMap(conn -> conn.rxSetAutoCommit(false)
                        .flatMap(v -> {
                            Set<Observable<UpdateResult>> collect = jsons
                                    .map(json -> {
                                        Integer state = json.getInteger("state");
                                        Boolean archive = json.getBoolean("ARCHIVE");
                                        return new JsonArray().add(state).add(json.encode()).add(archive);
                                    })
                                    .map(params -> conn.rxUpdateWithParams("INSERT EVENTS (state, document, ARCHIVE) VALUES (?,?,?)", params).toObservable())
                                    .collect(Collectors.toSet());
                            return Observable.concat(Observable.from(collect))
                                    .map(updateResult -> {
                                        int updated = updateResult.getUpdated();
                                        if (updated == 0) {
                                            throw new IllegalStateException("Impossible to insert one data");
                                        }
                                        return updated;
                                    })
                                    .reduce(0, (acc, a) -> acc + a)
                                    .toSingle();
                        })
                        .doOnError(err -> {
                            conn.rxRollback()
                                    .flatMap(v2 -> conn.rxClose());
                        })
                        .flatMap(rs -> conn.rxCommit()
                                .flatMap(v2 -> conn.rxClose())
                                .map(v3 -> rs)
                        ));
    }

    private static class RemoveByIdNititeFilter extends BaseFilter {

        private final Set<NitriteId> ids;

        private RemoveByIdNititeFilter(Set<NitriteId> ids) {
            this.ids = ids;
        }

        @Override
        public Set<NitriteId> apply(NitriteMap<NitriteId, Document> nitriteMap) {
            return ids;
        }
    }
}
