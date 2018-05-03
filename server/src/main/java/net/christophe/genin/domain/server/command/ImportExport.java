package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.mysql.MysqlCommand;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import org.dizitart.no2.*;
import org.dizitart.no2.filters.BaseFilter;
import org.dizitart.no2.internals.NitriteService;
import org.dizitart.no2.store.NitriteMap;
import rx.Observable;

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
            if (Dbs.instance.importFrom(msg.body())) {
                msg.reply(new JsonObject());
            } else {
                msg.fail(500, "Error in importing");
            }
        });

        vertx.eventBus().<JsonObject>consumer(EXPORT, (msg) -> {
            final NitriteCollection collection = Dbs.instance
                    .getCollection(Schemas.RAW_COLLECTION);
            List<Document> documents = collection
                    .find(eq(Schemas.RAW_STATE, Treatments.END.getState()))
                    .toList();
            Stream<JsonObject> obs = documents.stream()
                    .map(Dbs.Raws::toJson)
                    .map(json -> json.put("state", Treatments.END.getState()).put("ARCHIVE", true));
            if (documents.isEmpty()) {
                msg.reply(new JsonObject()
                        .put("numberOfExported", 0)
                        .put("numberOfDeleted", 0));
            } else {
                new MysqlCommand().exportEvents(obs)
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
                                    logger.error("Error in exporting to Mysql", err);
                                    msg.fail(500, "Error in exporting to Mysql");
                                }
                        );

            }
        });
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
