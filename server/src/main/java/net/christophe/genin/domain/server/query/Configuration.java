package net.christophe.genin.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.ConfigurationDto;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;

import java.util.Optional;

public class Configuration extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    public static final String EXPORTER = Configuration.class.getName() + ".exporter";
    public static final String GET = Configuration.class.getName() + ".get";

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
        vertx.eventBus().consumer(GET,
                msg -> {
                    ConfigurationDto conf = Optional.ofNullable(Dbs.instance
                            .repository(ConfigurationDto.class)
                            .find().firstOrDefault())
                            .orElseGet(ConfigurationDto::new);
                    msg.reply(Schemas.Configuration.toJson(conf));
                }

        );
        logger.info("started");
    }
}
