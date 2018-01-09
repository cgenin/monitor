package net.christophe.genin.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Queries;
import net.christophe.genin.domain.server.db.mysql.MysqlQuery;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.NitriteQuery;
import net.christophe.genin.domain.server.json.Jsons;

import java.util.Optional;

public class Projects extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Projects.class);

    public static final String LIST = Projects.class.getName() + ".list";
    public static final String GET = Projects.class.getName() + ".get";
    public static final String ID = "id";


    @Override
    public void start() {
        vertx.eventBus().consumer(LIST, msg -> {
            Queries.get().projects()
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error in " + LIST, err);
                                msg.fail(500, "Error in query");
                            }
                    );
        });
        vertx.eventBus().consumer(GET, (Handler<Message<JsonObject>>) msg -> {
            String id = msg.body().getString(ID, "");
            final JsonArray l = Dbs.instance.getCollection(Schemas.Version.collection(id))
                    .find().toList()
                    .parallelStream()
                    .map(doc -> {
                        final Dbs.Attributes attributes = new Dbs.Attributes(doc);
                        JsonObject put = new JsonObject()
                                .put(Schemas.Version.id.name(), doc.get(Schemas.Version.id.name()))
                                .put(Schemas.Version.name.name(), doc.get(Schemas.Version.name.name()))
                                .put(Schemas.Version.isSnapshot.name(), doc.get(Schemas.Version.isSnapshot.name()))
                                .put(Schemas.Version.changelog.name(), doc.get(Schemas.Version.changelog.name()))
                                .put(Schemas.Version.latestUpdate.name(), doc.get(Schemas.Version.latestUpdate.name()))
                                .put(Schemas.Version.tables.name(), attributes.toJsonArray(Schemas.Version.tables.name()))
                                .put(Schemas.Version.apis.name(), attributes.toJsonArray(Schemas.Version.apis.name()))
                                .put(Schemas.Version.javaDeps.name(), attributes.toJsonArray(Schemas.Version.javaDeps.name()));
                        return put;
                    }).collect(Jsons.Collectors.toJsonArray());
            if (logger.isDebugEnabled()) {
                logger.debug("GET : " + id + " -res :" + l.encodePrettily());
            }
            msg.reply(l);
        });

        logger.info("started");
    }
}
