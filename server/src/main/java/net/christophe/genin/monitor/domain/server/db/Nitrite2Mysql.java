package net.christophe.genin.monitor.domain.server.db;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.monitor.domain.server.Database;
import net.christophe.genin.monitor.domain.server.Server;
import net.christophe.genin.monitor.domain.server.db.migration.MigrateConfiguration;
import net.christophe.genin.monitor.domain.server.db.migration.MigrateInQueue;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.Database;
import net.christophe.genin.monitor.domain.server.Server;
import rx.Observable;
import rx.Single;

import java.util.Objects;

public class Nitrite2Mysql extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Nitrite2Mysql.class);

    @Override
    public void start() {
        Observable.concat(
                nitriteLoading(),
                createMysqlConnections(),
                toObservable(vertx.eventBus().rxSend(MigrateConfiguration.LAUNCH, new JsonObject())),
                vertx.eventBus().<JsonArray>rxSend(MigrateInQueue.LAUNCH, new JsonObject())
                        .map(Message::body)
                        .toObservable()
                        .flatMap(arr -> Observable.from(Jsons.builder(arr).toListString()))

        ).subscribe(
                logger::info,
                err -> {
                    logger.error("Error in migration", err);
                    vertx.eventBus().send(Server.FAIL, new JsonObject());
                },
                () -> {
                    logger.info("Completed work !!!");
                    vertx.eventBus().send(Server.STOP, new JsonObject());
                });


    }

    private Observable<String> toObservable(Single<Message<String>> single) {
        return single.map(Message::body)
                .toObservable();
    }

    private Observable<String> createMysqlConnections() {
        return Database.runMysql(vertx)
                .map(Mysqls::active)
                .map(active -> {
                    if (!active) {
                        throw new IllegalStateException("Impossible to save connection for MysqlPort");
                    }
                    return "MysqlPort db connection Ok.";
                }).toObservable();
    }

    private Observable<String> nitriteLoading() {
        return Single.fromCallable(() -> {
            NitriteDbs nitriteDbs = Database.nitriteLoading(config());
            if (Objects.isNull(nitriteDbs)) {
                throw new IllegalStateException("Impossible to load nitrite db with " + config().encode());
            }
            return "nitrite Db Ok.";
        }).toObservable();
    }
}
