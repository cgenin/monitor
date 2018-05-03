package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.stream.Collectors;

public class Raw extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Raw.class);

    public static final String SAVING = Raw.class.getName() + ".saving";
    public static final String CLEAR_CALCULATE_DATA = Raw.class.getName() + ".clear.calculate.data";


    @Override
    public void start() {
        logger.info("start Raw Verticle");
        vertx.eventBus().<JsonObject>consumer(SAVING, rc -> {
            final JsonObject body = rc.body();

            Single.fromCallable(() -> {
                final Document document = Dbs.Raws.toDoc(body)
                        .put("state", Treatments.PROJECTS.getState());

                Dbs.instance.getCollection(Schemas.RAW_COLLECTION)
                        .insert(document);
                return document.getId().getIdValue();
            })
                    .observeOn(Schedulers.io())
                    .subscribe(
                            rc::reply,
                            err -> {
                                logger.error("Error in saving events", err);
                                rc.fail(500, "Error in saving events");
                            }
                    );
        });
        vertx.eventBus().consumer(CLEAR_CALCULATE_DATA, rc -> {

            Observable<String> listIdVersions = Observable.from(Dbs.instance.getCollection(Schemas.Projects.collection())
                    .find()
                    .toList()
                    .stream()
                    .map(d -> d.get(Schemas.Projects.id.name(), String.class))
                    .map(Schemas.Version::collection)
                    .collect(Collectors.toList()));

            Observable<String> others = Observable.just(Schemas.Apis.collection(), Schemas.Projects.collection(), Schemas.Tables.collection());

            Observable.concat(listIdVersions, others)
                    .observeOn(Schedulers.io())
                    .map(idCollection -> {
                        NitriteCollection collection = Dbs.instance.getCollection(idCollection);
                        long size = collection.size();
                        collection.drop();
                        return new JsonObject().put("collectionName", idCollection).put("size", size);
                    })
                    .reduce(new JsonArray(), JsonArray::add)
                    .subscribe(
                            rc::reply,
                            err -> {
                                logger.error("Error in clearing in calculating data", err);
                                rc.fail(500, "Error in clearing in calculating data");
                            });
        });

    }
}
