package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.mysql.Events;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import org.dizitart.no2.Document;
import rx.Single;

import java.util.Optional;
import java.util.function.Supplier;

public class Raw extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Raw.class);

    public static final String SAVING = Raw.class.getName() + ".saving";


    @Override
    public void start() throws Exception {
        logger.info("start Raw Verticle");
        vertx.eventBus().consumer(SAVING, (Handler<Message<JsonObject>>) rc -> {
            final JsonObject body = rc.body();

            Mysqls mysqls = Mysqls.Instance.get();
            Supplier<Single<Long>> supplier = (mysqls.active()) ? () -> Events.insert(body) : () -> nitrite(body);
            supplier.get().subscribe(
                    rc::reply,
                    err -> {
                        logger.error("Error in saving events", err);
                        rc.fail(500, "Error in saving events");
                    }
            );
        });
    }


    private Single<Long> nitrite(JsonObject body) {
        final Document document = Dbs.Raws.toDoc(body)
                .put("state", Treatments.PROJECTS.getState());

        Dbs.instance.getCollection(Schemas.RAW_COLLECTION)
                .insert(document);
        return Single.just(document.getId().getIdValue());
    }
}
