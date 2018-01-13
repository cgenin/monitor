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
        vertx.eventBus().consumer(LIST,
                msg -> Queries.get().projects()
                        .subscribe(
                                msg::reply,
                                err -> {
                                    logger.error("error in " + LIST, err);
                                    msg.fail(500, "Error in query");
                                }
                        ));
        vertx.eventBus().consumer(GET, (Handler<Message<JsonObject>>) msg -> {
            String id = msg.body().getString(ID, "");
            Queries.get().versions(id)
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error in " + GET, err);
                                msg.fail(500, "Error in query");
                            }
                    );
        });

        logger.info("started");
    }
}
