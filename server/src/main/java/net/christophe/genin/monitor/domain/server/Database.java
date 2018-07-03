package net.christophe.genin.monitor.domain.server;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.eventbus.Message;
import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import net.christophe.genin.monitor.domain.server.db.mysql.FlywayVerticle;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.model.Configuration;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Single;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Class for db initialization.
 * Used to load Nitrite or mysql db.
 */
public class Database extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    public static final String HEALTH = Database.class.getName() + ".health";
    public static final String MYSQL_CREATE_SCHEMA = Database.class.getName() + ".mysql.save.schema";
    public static final String MYSQL_ON_OFF = Database.class.getName() + ".mysql.on.off";
    public static final String TEST_MYSQL_CONNECTION = Database.class.getName() + ".mysql.test.connection";

    /**
     * load nitrite db
     *
     * @param config the server configuration.
     * @return the instance.
     */
    public static NitriteDbs nitriteLoading(JsonObject config) {
        JsonObject dbConfig = config.getJsonObject("nitritedb", new JsonObject());
        String dbPath = dbConfig.getString("path", "test.db");
        String user = dbConfig.getString("user", "user");
        String pwd = dbConfig.getString("pwd", "password");
        return NitriteDbs.instance.build(dbPath, user, pwd);
    }

    /**
     * load  mysql datasource.
     *
     * @param vertx vertx impl.
     * @return the result of the loading.
     */
    public static Single<Mysqls> runMysql(Vertx vertx) {
        return Configuration.load()
                .map(configuration -> {
                    if (Objects.isNull(configuration.mysqlUser()))
                        throw new IllegalStateException("No configuration found");
                    JsonObject config = new JsonObject()
                            .put("host", configuration.mysqlHost())
                            .put("port", configuration.mysqlPort())
                            .put("username", configuration.mysqlUser())
                            .put("password", configuration.mysqlPassword())
                            .put("database", configuration.mysqlDB());
                    return Mysqls.Instance.set(vertx, config);
                })
                .doOnSuccess(instance -> {
                    Adapters.Type type = (instance.active()) ? Adapters.Type.MYSQL : Adapters.Type.NITRITE;
                    vertx.eventBus().send(Adapters.CHANGE_DATABASE, type.name());
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
                        instance -> logger.info("MysqlPort active : " + instance.active()),
                        err -> {
                            logger.error("Impossible to open connection to mysql => " + err.getMessage());
                            logger.debug("Error in activating mysql.", err);
                            Mysqls.Instance.disabled();
                        }
                );
        eventBusEndpoints();

        logger.info("started");
    }

    @Override
    public void stop() {
        NitriteDbs.instance.close();
    }

    /**
     * Create Event bus endpoints.
     */
    public void eventBusEndpoints() {
        // Health endpoints
        vertx.eventBus().consumer(HEALTH, msg -> {
            JsonArray health = NitriteDbs.toArray(
                    NitriteDbs.instance.getCollection("health")
                            .find()
                            .toList()
            );

            msg.reply(new JsonObject().put("health", health).put("mysql", Mysqls.Instance.get().active()));

        });
        // mysql db schema creation
        vertx.eventBus().consumer(MYSQL_CREATE_SCHEMA, msg -> {
            Mysqls mysqls = Mysqls.Instance.get();
            if (mysqls.active()) {
                vertx.eventBus().<JsonObject>rxSend(FlywayVerticle.UPGRADE, mysqls.configuration())
                        .map(Message::body)
                        .subscribe(
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
        // start / stop MysqlPort db connection
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

        vertx.eventBus().<JsonObject>consumer(TEST_MYSQL_CONNECTION, msg -> {
            JsonObject configuration = msg.body();
            Single.just(configuration)
                    .map(c -> c.put("username", c.getString("user")))
                    .flatMap(c -> Mysqls.test(vertx, c))
                    .subscribe(
                            bool -> {
                                if (bool) {
                                    msg.reply(new JsonObject().put("state", "success"));
                                } else {
                                    msg.reply(new JsonObject().put("state", "fail").put("msgError", "Impossible de se connecter à la base de données."));
                                }
                            },
                            err -> {
                                logger.error("Error in testing ", err);

                                StringWriter sw = new StringWriter();
                                err.printStackTrace(new PrintWriter(sw, true));
                                String stacktrace = sw.getBuffer().toString();
                                msg.reply(new JsonObject()
                                        .put("state", "fail")
                                        .put("msgError", "Un erreur est survenue.")
                                        .put("stacktrace", stacktrace)
                                );
                            }
                    );
        });
    }


}
