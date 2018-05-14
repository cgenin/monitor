package net.christophe.genin.monitor.domain.server.migration;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import org.dizitart.no2.NitriteCollection;
import rx.Observable;

import java.util.HashSet;


public class MigrateInQueue extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MigrateInQueue.class);
    public static final String LAUNCH = MigrateInQueue.class.getName() + ".launch";

    @Override
    public void start() {
        logger.info("initialization Ok.");
        vertx.eventBus().consumer(LAUNCH, msg -> {
            final NitriteCollection collection = NitriteDbs.instance
                    .getCollection(Schemas.RAW_COLLECTION);
            logger.info("Size of migration events :" + collection.size());
            Observable.from(
                    collection
                            .find()
                            .toList())
                    .map(doc -> {
                        JsonObject document = NitriteDbs.Raws.toJson(doc);
                        Integer state = doc.get(Schemas.RAW_STATE, Integer.class);
                        return new JsonObject()
                                .put(Schemas.RAW_STATE, state)
                                .put("document", document.encode());
                    })

                    .reduce(new HashSet<String>(), (acc, obj) -> {
                        acc.add(obj.encode());
                        return acc;
                    })
                    .flatMap(Observable::from)
                    .flatMap(str -> {
                        JsonObject entries = new JsonObject(str);
                        JsonArray params = new JsonArray()
                                .add(entries.getInteger(Schemas.RAW_STATE))
                                .add(entries.getString("document"));
                        return Mysqls.Instance.get()
                                .execute("INSERT INTO QUEUE (state, document) VALUES (?,?)", params)
                                .map(updateResult -> {
                                    if (updateResult.getUpdated() == 1) {
                                        JsonArray keys = updateResult.getKeys();
                                        return "Inserted in Queue " + str + "with value " + keys.encode();
                                    }
                                    return "Failed to insert " + str;
                                }).toObservable();
                    })
                    .reduce(new JsonArray(), JsonArray::add)
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("Error in migrating ", err);
                                msg.fail(500, "Migrate ConfigurationQuery problem see below");
                            });
        });
    }
}
