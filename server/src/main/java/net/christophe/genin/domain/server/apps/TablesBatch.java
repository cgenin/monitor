package net.christophe.genin.domain.server.apps;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class TablesBatch extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(TablesBatch.class);

    @Override
    public void start() throws Exception {
        final long batch = Treatments.batchTime(config());
        logger.info("batch time : " + batch);
        vertx.setPeriodic(batch, (id) -> periodic());

        logger.info("started.");
    }

    private void periodic() {

    }
}
