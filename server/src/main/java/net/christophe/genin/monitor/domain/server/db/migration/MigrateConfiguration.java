package net.christophe.genin.monitor.domain.server.db.migration;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.Message;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.query.ConfigurationQuery;

public class MigrateConfiguration extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(MigrateConfiguration.class);
    public static final String LAUNCH = MigrateConfiguration.class.getName() + ".launch";

    @Override
    public void start() {
        logger.info("initialization Ok.");
        vertx.eventBus().consumer(LAUNCH, msg -> {
            try {
                vertx.eventBus().<JsonObject>rxSend(ConfigurationQuery.GET, new JsonObject())
                        .flatMap(jsonObject -> {
                            Mysqls mysqls = Mysqls.Instance.get();
                            return mysqls.execute("DELETE FROM configuration ")
                                    .flatMap(updateResult -> {
                                        logger.info("Nunber of deleted row : " + updateResult.getUpdated());
                                        JsonArray params = new JsonArray().add(1).add(jsonObject.body().encode());
                                        return mysqls.execute("INSERT INTO configuration (ID, document) VALUES (?,?)", params);
                                    });
                        })
                        .map(updateResult -> {
                            if (updateResult.getUpdated() != 1) {
                                throw new IllegalStateException("impossible to store data configuration");
                            }
                            return "migrate configuration : Ok.";
                        })
                        .subscribe(msg::reply, err -> errorReply(msg, err));

            } catch (Exception ex) {
                errorReply(msg, ex);
            }
        });


    }

    public void errorReply(Message<Object> msg, Throwable ex) {
        logger.error("Error in migrating ", ex);
        msg.fail(500, "Migrate ConfigurationQuery problem see below");
    }
}
