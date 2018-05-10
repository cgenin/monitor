package net.christophe.genin.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.model.Configuration;

import java.util.Collections;
import java.util.Optional;

/**
 * Read Operation on ConfigurationQuery.
 */
public class ConfigurationQuery extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationQuery.class);

    public static final String GET = ConfigurationQuery.class.getName() + ".get";


    private static JsonObject toJson(Configuration configuration) {
        JsonObject mysql = new JsonObject()
                .put("host", configuration.getMysqlHost())
                .put("port", configuration.getMysqlPort())
                .put("user", configuration.getMysqlUser())
                .put("password", configuration.getMysqlPassword())
                .put("database", configuration.getMysqlDB());
        JsonArray javaFilters = Optional.ofNullable(configuration.getJavaFilters()).orElse(Collections.emptyList())
                .parallelStream().collect(Jsons.Collectors.toJsonArray());
        JsonArray npmFilters = Optional.ofNullable(configuration.getNpmFilters()).orElse(Collections.emptyList())
                .parallelStream().collect(Jsons.Collectors.toJsonArray());
        return new JsonObject()
                .put("id", configuration.getConfId())
                .put("javaFilters", javaFilters)
                .put("npmFilters", npmFilters)
                .put("mysql", mysql);
    }

    @Override
    public void start() {

        vertx.eventBus().consumer(GET,
                msg -> {
                    Configuration.load()
                            .map(ConfigurationQuery::toJson)
                            .subscribe(
                                    msg::reply,
                                    (ex) -> {
                                        logger.error("Error in loading conf", ex);
                                        msg.fail(500, "Error in loading conf");
                                    }
                            );
                }

        );
        logger.info("started");
    }


}
