package net.christophe.genin.domain.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;

import java.util.Date;
import java.util.Optional;

public class InitializeDb extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(InitializeDb.class);

    public static final String HEALTH = InitializeDb.class.getName() + ".health";



    @Override
    public void start() throws Exception {
        JsonObject dbConfig = config().getJsonObject("db", new JsonObject());
        String dbPath = dbConfig.getString("path", "test.db");
        String user = dbConfig.getString("user", "user");
        String pwd = dbConfig.getString("pwd", "password");


        NitriteCollection testCollection = Dbs.instance.build(dbPath, user, pwd).getCollection("health");

        Document init = Optional.ofNullable(testCollection.find().firstOrDefault())
                .orElseGet(() -> Document.createDocument("db", true))
                .put("time", new Date().getTime());

        logger.info("Updated : " + testCollection.update(init, true).getAffectedCount());

        vertx.eventBus().consumer(HEALTH, msg -> {
            JsonArray health = Dbs.toArray(
                    Dbs.instance.getCollection("health")
                            .find()
                            .toList()
            );
            msg.reply(health);

        });

    }

}
