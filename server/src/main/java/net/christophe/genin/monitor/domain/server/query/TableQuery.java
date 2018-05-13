package net.christophe.genin.monitor.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.command.RawCommand;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.model.Table;
import net.christophe.genin.monitor.domain.server.command.RawCommand;
import rx.Observable;

import java.util.HashMap;

public class TableQuery extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(RawCommand.class);

    public static final String LIST = TableQuery.class.getName() + ".list";
    public static final String BY_PROJECT = TableQuery.class.getName() + ".by.project";

    @Override
    public void start() {

        vertx.eventBus().consumer(LIST, msg -> {
            Table.findAll()
                    .reduce(new HashMap<String, JsonObject>(),
                            (m, row) -> {
                                String name = row.tableName();
                                String service = row.service();
                                Long update = row.lastUpdated();
                                JsonObject obj = m.getOrDefault(name, new JsonObject()
                                        .put(Schemas.Tables.id.name(), name)
                                        .put(Schemas.Tables.name.name(), name)
                                        .put(Schemas.Tables.latestUpdate.name(), update)
                                );
                                JsonArray services = obj.getJsonArray(Schemas.Tables.services.name(), new JsonArray())
                                        .add(service);
                                obj.put(Schemas.Tables.services.name(), services);
                                m.put(name, obj);
                                return m;
                            })
                    .flatMap(hasmap -> Observable.from(hasmap.values()))
                    .reduce(new JsonArray(), JsonArray::add)
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error in " + LIST, err);
                                msg.fail(500, "Error in query");
                            }
                    );

        });

        vertx.eventBus().consumer(BY_PROJECT, msg -> {
            Table.countTablesByProjects()
                    .flatMap(hasmap -> Observable.from(hasmap.entrySet()))
                    .reduce(new JsonObject(), (acc, e) -> acc.put(e.getKey(), e.getValue()))
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error in " + BY_PROJECT, err);
                                msg.fail(500, "Error in query");
                            }
                    );
        });

        logger.info("started");
    }
}
