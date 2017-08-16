package net.christophe.genin.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Dbs;

public class Configuration extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    public static final String EXPORTER = Projects.class.getName() + ".exporter";

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(EXPORTER, msg -> Dbs.instance.exporter()
                .subscribe(
                        msg::reply,
                        (ex) -> {
                            logger.error("Error in Export", ex);
                            msg.fail(500, "Error in Export");
                        }
                ));
        logger.info("started");
    }
}
