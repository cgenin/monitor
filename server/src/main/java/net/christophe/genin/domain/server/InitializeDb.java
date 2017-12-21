package net.christophe.genin.domain.server;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
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

public class InitializeDb extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(InitializeDb.class);

    public static final String HEALTH = InitializeDb.class.getName() + ".health";
    public static final String MYSQL_CREATE_SCHEMA = InitializeDb.class.getName() + ".mysql.create.schema";
    public static final String MYSQL_ON_OFF = InitializeDb.class.getName() + ".mysql.on.off";


    @Override
    public void start() {
        JsonObject dbConfig = config().getJsonObject("db", new JsonObject());
        String dbPath = dbConfig.getString("path", "test.db");
        String user = dbConfig.getString("user", "user");
        String pwd = dbConfig.getString("pwd", "password");


        NitriteCollection testCollection = Dbs.instance.build(dbPath, user, pwd).getCollection("health");

        Document init = Optional.ofNullable(testCollection.find().firstOrDefault())
                .orElseGet(() -> Document.createDocument("db", true))
                .put("time", new Date().getTime());

        logger.info("Updated : " + testCollection.update(init, true).getAffectedCount());

        runMysql().subscribe(instance -> logger.info("Mysql active : " + instance.active()),
                err -> logger.error("Error in activating mysql.", err));

        vertx.eventBus().consumer(HEALTH, msg -> {
            JsonArray health = Dbs.toArray(
                    Dbs.instance.getCollection("health")
                            .find()
                            .toList()
            );

            msg.reply(new JsonObject().put("health", health).put("mysql", Mysqls.Instance.get().active()));

        });
        vertx.eventBus().consumer(MYSQL_CREATE_SCHEMA, msg -> {
            Mysqls mysqls = Mysqls.Instance.get();
            if (mysqls.active()) {
                AntiMonitorSchemas.create().subscribe((report) -> {
                    msg.reply(new JsonObject()
                            .put("active", true)
                            .put("creation", true)
                            .put("report", report)
                    );
                }, err -> {
                    logger.error("Error in creating table", err);
                    msg.fail(500, "Error in creating table");
                });
            } else {
                msg.reply(new JsonObject().put("active", false).put("creation", false));
            }

        });
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
                runMysql().subscribe(instance -> {
                    boolean active = instance.active();
                    toOuput.accept(active);
                }, err -> {
                    logger.error("error in mysql on/off ", err);
                    msg.fail(500, "error in mysql on/off ");
                });
            }
        });
    }

    private Single<Mysqls> runMysql() {
        return Single.just(Configuration.get())
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

}
