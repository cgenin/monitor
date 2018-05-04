package net.christophe.genin.domain.server;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.domain.server.db.mysql.AntiMonitorSchemas;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.query.Configuration;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Single;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Class for db initialization.
 * Used to load Nitrite or mysql db.
 */
public class InitializeDb extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(InitializeDb.class);

    public static final String HEALTH = InitializeDb.class.getName() + ".health";
    public static final String MYSQL_CREATE_SCHEMA = InitializeDb.class.getName() + ".mysql.save.schema";
    public static final String MYSQL_ON_OFF = InitializeDb.class.getName() + ".mysql.on.off";

    /**
     * load nitrite db
     *
     * @param config the server configuration.
     * @return the instance.
     */
    public static Dbs nitriteLoading(JsonObject config) {
        JsonObject dbConfig = config.getJsonObject("db", new JsonObject());
        String dbPath = dbConfig.getString("path", "test.db");
        String user = dbConfig.getString("user", "user");
        String pwd = dbConfig.getString("pwd", "password");
        return Dbs.instance.build(dbPath, user, pwd);
    }

    /**
     * load  mysql datasource.
     *
     * @param vertx vertx impl.
     * @return the result of the loading.
     */
    public static Single<Mysqls> runMysql(Vertx vertx) {
        return Single.fromCallable(Configuration::get)
                .map(configurationDto -> {
                    if (Objects.isNull(configurationDto.getMysqlUser()))
                        return Mysqls.Instance.get();
                    JsonObject config = new JsonObject()
                            .put("host", configurationDto.getMysqlHost())
                            .put("port", configurationDto.getMysqlPort())
                            .put("username", configurationDto.getMysqlUser())
                            .put("password", configurationDto.getMysqlPassword())
                            .put("database", configurationDto.getMysqlDB());
                    return Mysqls.Instance.set(vertx, config);
                });
    }

    @Override
    public void start() {
        // load nitrite and test access
        NitriteCollection testCollection = nitriteLoading(config()).getCollection("health");
        Document init = Optional.ofNullable(testCollection.find().firstOrDefault())
                .orElseGet(() -> Document.createDocument("db", true))
                .put("time", new Date().getTime());

        logger.info("Updated : " + testCollection.update(init, true).getAffectedCount());
        // load mysql database
        runMysql(vertx)
                .subscribe(
                        instance -> logger.info("Mysql active : " + instance.active()),
                        err -> logger.error("Error in activating mysql.", err)
                );
        eventBusEndpoints();


    }

    /**
     * Create Event bus endpoints.
     */
    private void eventBusEndpoints() {
        // Health endpoints
        vertx.eventBus().consumer(HEALTH, msg -> {
            JsonArray health = Dbs.toArray(
                    Dbs.instance.getCollection("health")
                            .find()
                            .toList()
            );

            msg.reply(new JsonObject().put("health", health).put("mysql", Mysqls.Instance.get().active()));

        });
        // mysql db schema creation
        vertx.eventBus().consumer(MYSQL_CREATE_SCHEMA, msg -> {
            Mysqls mysqls = Mysqls.Instance.get();
            if (mysqls.active()) {
                AntiMonitorSchemas.create().subscribe(
                        (report) -> msg.reply(new JsonObject()
                                .put("active", true)
                                .put("creation", true)
                                .put("report", report)
                        ),
                        err -> {
                            logger.error("Error in creating table", err);
                            msg.fail(500, "Error in creating table");
                        });
            } else {
                msg.reply(new JsonObject().put("active", false).put("creation", false));
            }

        });
        // start / stop Mysql db connection
        vertx.eventBus().consumer(MYSQL_ON_OFF, msg -> {
            Consumer<Boolean> toOuput = (active) -> {
                msg.reply(new JsonObject().put("active", active));
                String state = (active) ? "Actif" : "Désactivé";
                vertx.eventBus().send(Console.INFO, "Etat de mysql " + state);
            };
            if (Mysqls.Instance.get().active()) {
                // disabled mysql db
                toOuput.accept(Mysqls.Instance.disabled());
            } else {
                runMysql(vertx)
                        .subscribe(
                                instance -> {
                                    boolean active = instance.active();
                                    toOuput.accept(active);
                                },
                                err -> {
                                    logger.error("error in mysql on/off ", err);
                                    msg.fail(500, "error in mysql on/off ");
                                });
            }
        });
    }


}
