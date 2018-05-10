package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.domain.server.model.Configuration;

public class ConfigurationCommand extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationCommand.class);

    public static final String SAVE = ConfigurationCommand.class.getName() + ".save";

    @Override
    public void start() {
        vertx.eventBus().consumer(SAVE, (Handler<Message<JsonObject>>) (msg) -> {
            JsonObject body = msg.body();
            Configuration.load()
                    .map(configuration -> {
                        JsonObject mysql = body.getJsonObject("mysql", new JsonObject());
                        configuration.setConfId(body.getLong("id", 0L))
                                .setMysqlHost(mysql.getString("host"))
                                .setMysqlPort(mysql.getInteger("port"))
                                .setMysqlUser(mysql.getString("user"))
                                .setMysqlPassword(mysql.getString("password"))
                                .setMysqlDB(mysql.getString("database"))
                                .setJavaFilters(Jsons.builder(body.getJsonArray("getJavaFilters")).toListString())
                                .setNpmFilters(Jsons.builder(body.getJsonArray("getNpmFilters")).toListString());
                        return configuration;
                    })
                    .flatMap(Configuration::save)
                    .subscribe(bool -> {
                                logger.info("Configuration saved : " + bool);
                                msg.reply(body);
                            },
                            err -> {
                                logger.error("error in saving configuration ", err);
                                msg.fail(500, "Error in saving configuration");
                            });
        });
    }
}
