package net.christophe.genin.domain.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Db extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Db.class);

    public static final String HEALTH = Db.class.getName() + ".health";

    @Override
    public void start() throws Exception {
        String dbPath = config().getString("dbPath", "test.db");
        String user = config().getString("userDb", "user");
        String pwd = config().getString("pwdDb", "password");

        Nitrite db = Nitrite.builder()
                .compressed()
                .filePath(dbPath)
                .openOrCreate(user, pwd);

        NitriteCollection testCollection = db.getCollection("health");

        Document init = Optional.ofNullable(testCollection.find().firstOrDefault())
                .orElseGet(() -> Document.createDocument("db", true))
                        .put("time", new Date().getTime());

        logger.info("Updated : " + testCollection.update(init, true).getAffectedCount());

        vertx.eventBus().consumer(HEALTH, msg -> {
            List<JsonObject> health = db.getCollection("health").find()
                    .toList()
                    .stream()
                    .map(doc -> doc.keySet()
                            .parallelStream()
                            .map(key -> new JsonObject().put(key, doc.get(key)))
                            .reduce(JsonObject::mergeIn).orElse(new JsonObject())
                    )
                    .collect(Collectors.toList());
            msg.reply(new JsonArray(health));

        });

    }
}
