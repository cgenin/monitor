package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.adapter.nitrite.NitriteRaw;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import org.dizitart.no2.Document;
import rx.Single;
import rx.schedulers.Schedulers;

public class Front extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Front.class);

    public static final String SAVING = Front.class.getName() + ".saving";

    enum State {
        FRONT(0), END(1);

        private final int state;

        State(int state) {
            this.state = state;
        }
    }

    @Override
    public void start() {
        logger.info("start RawCommand Verticle");
        vertx.eventBus().consumer(SAVING, (Handler<Message<JsonObject>>) rc -> {
            final JsonObject body = rc.body();

            Single.fromCallable(() -> {
                final Document document = NitriteRaw.toDoc(body)
                        .put("state", State.FRONT.state);

                NitriteDbs.instance.getCollection(Schemas.FRONT_COLLECTION)
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
    }
}
