package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.Console;
import rx.Observable;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Supplier;

/**
 * State of all tasks for all differents screens.
 * <p>Contains also methods for launching periodic treatment</p>
 */
public enum Treatments {
    PROJECTS(0),
    TABLES(1),
    VERSION(2),
    URL(3),
    DEPENDENCIES(4),
    END(5);


    private static final Logger logger = LoggerFactory.getLogger(Treatments.class);


    private final Integer state;

    Treatments(Integer state) {
        this.state = state;
    }

    public Integer getState() {
        return state;
    }

    public static long batchTime(JsonObject config) {
        final Long batch = config.getLong("batch", 1_000L);
        Random r = new Random();
        final Long noise = r.longs(1_000L, 4_001L)
                .findFirst()
                .getAsLong();
        return noise + batch;
    }

    public static Treatments parse(Integer state) {
        return Arrays.stream(Treatments.values())
                .filter(treatments -> treatments.getState().equals(state))
                .findFirst()
                .orElseGet(() -> {
                    logger.warn("Treatments not found : " + state + " return end by default");
                    return END;
                });
    }

    /**
     * The class for launching periodic treatments.
     */
    public static class Periodic {

        private final AbstractVerticle verticle;
        private final Logger logger;

        public Periodic(AbstractVerticle verticle, Logger logger) {
            this.verticle = verticle;
            this.logger = logger;
        }

        public void run(Supplier<Observable<String>> supplier) {
            final long batch = Treatments.batchTime(verticle.config());
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
}
