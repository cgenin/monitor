package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.model.FrontApps;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Date;

public class FrontCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(FrontCommand.class);

    public static final String SAVING = FrontCommand.class.getName() + ".saving";



    @Override
    public void start() {
        logger.info("start FrontCommand Verticle");
        vertx.eventBus().consumer(SAVING, (Handler<Message<JsonObject>>) rc -> {
            final JsonObject body = rc.body();
            String artifactId = body.getString("artifactId", "");
            String groupId = body.getString("groupId", "");
            String version = body.getString("version", "");
            JsonObject packagesJson = body.getJsonObject("packagesJson", new JsonObject());
            FrontApps.findBy(groupId, artifactId, version)
                    .observeOn(Schedulers.io())
                    .map(fa -> fa.setPackageJson(packagesJson).setLastUpdate(new Date().getTime()))
                    .switchIfEmpty(Observable.fromCallable(() -> FrontApps.newInstance(packagesJson, groupId, artifactId, version)))
                    .toSingle()
                    .flatMap(FrontApps::save)
                    .subscribe(
                            rc::reply,
                            err -> {
                                logger.error("Error in saving fronts", err);
                                rc.fail(500, "Error in saving fronts");
                            }
                    );
        });
    }
}
