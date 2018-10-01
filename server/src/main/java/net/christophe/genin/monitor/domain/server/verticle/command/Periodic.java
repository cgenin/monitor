package net.christophe.genin.monitor.domain.server.verticle.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import net.christophe.genin.monitor.domain.server.verticle.Console;
import rx.Observable;

import java.util.Random;
import java.util.function.Supplier;

public class Periodic {

    private final AbstractVerticle verticle;
    private final Logger logger;

    public Periodic(AbstractVerticle verticle, Logger logger) {
        this.verticle = verticle;
        this.logger = logger;
    }

    public static long batchTime(JsonObject config) {
        final Long batch = config.getLong("batch", 1_000L);
        Random r = new Random();
        final Long noise = r.longs(1_000L, 4_001L)
                .findFirst()
                .getAsLong();
        return noise + batch;
    }

    public void run(Supplier<Observable<String>> supplier) {
        final long batch = batchTime(verticle.config());
        logger.info("batch time : " + batch);
        verticle.getVertx().setPeriodic(batch, (id) -> {
            logger.debug("Début du traitement");
            supplier.get().subscribe(
                    str -> {
                        logger.info(str);
                        verticle.getVertx().eventBus().send(Console.INFO, str);
                    },
                    err -> {
                        logger.error("error in tables for ", err);
                    },
                    () -> logger.debug("Fin du traitement")
            );
        });
        logger.info("started.");
    }

}
