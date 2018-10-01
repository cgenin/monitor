package net.christophe.genin.monitor.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.monitor.domain.boundedcontext.domain.ConfigurationDomain;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IConfiguration;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Configuration;
import net.christophe.genin.monitor.domain.server.db.adapter.Adapters;

import java.util.Collections;
import java.util.Optional;

/**
 * Read Operation on Configuration.
 */
public class ConfigurationQuery extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationQuery.class);

    public static final String GET = ConfigurationQuery.class.getName() + ".get";


    private static JsonObject toJson(Configuration configuration) {
        JsonObject mysql = new JsonObject()
                .put("host", configuration.mysqlHost())
                .put("port", configuration.mysqlPort())
                .put("user", configuration.mysqlUser())
                .put("password", configuration.mysqlPassword())
                .put("database", configuration.mysqlDB());
        JsonArray javaFilters = Optional.ofNullable(configuration.javaFilters()).orElse(Collections.emptyList())
                .parallelStream().collect(Jsons.Collectors.toJsonArray());
        JsonArray ignoreJava = Optional.ofNullable(configuration.ignoreJava()).orElse(Collections.emptyList())
                .parallelStream().collect(Jsons.Collectors.toJsonArray());
        JsonArray npmFilters = Optional.ofNullable(configuration.npmFilters()).orElse(Collections.emptyList())
                .parallelStream().collect(Jsons.Collectors.toJsonArray());
        return new JsonObject()
                .put("id", configuration.confId())
                .put("moniThorUrl", configuration.monithorUrl())
                .put("javaFilters", javaFilters)
                .put("npmFilters", npmFilters)
                .put("ignoreJava", ignoreJava)
                .put("mysql", mysql);
    }

    @Override
    public void start() {

        vertx.eventBus().consumer(GET,
                msg -> {
                    IConfiguration iConfiguration = Adapters.get().configurationHandler();
                    new ConfigurationDomain(iConfiguration).load()
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
