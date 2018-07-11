package net.christophe.genin.monitor.domain.server.http;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import net.christophe.genin.domain.monitor.addon.http.EventBusReplier;
import net.christophe.genin.domain.monitor.addon.http.Https;
import net.christophe.genin.monitor.domain.server.Database;
import net.christophe.genin.monitor.domain.server.query.*;
import net.christophe.genin.monitor.domain.server.command.*;

import java.util.Date;
import java.util.Objects;

/**
 * Api rest builder.
 */
public class Services {
    private static final Logger logger = LoggerFactory.getLogger(Services.class);
    private final Vertx vertx;

    /**
     * Builder contructor.
     *
     * @param vertx vertx
     */
    public Services(Vertx vertx) {
        this.vertx = vertx;
    }


    /**
     * Creations of all endpoints in on router.
     *
     * @return the router
     */
    public Router build() {
        logger.info("building router....");
        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Content-Type"));
        router.get("/_health").handler(rc ->
                EventBusReplier.builder(vertx, rc)
                        .json().adress(Database.HEALTH).withNoBody()
        );
        router.mountSubRouter("/projects", projects());
        router.mountSubRouter("/tables", tables());
        router.mountSubRouter("/endpoints", apis());
        router.mountSubRouter("/dependencies", dependencies());
        router.mountSubRouter("/configuration", configuration());
        router.mountSubRouter("/dump", dump());
        router.mountSubRouter("/apps", apps());
        router.mountSubRouter("/fronts", fronts());
        logger.info("building router : OK.");

        return router;
    }

    /**
     * Apis  endpoints.
     *
     * @return the router
     */
    private Router apis() {
        Router router = Router.router(vertx);
        router.get("/").handler(rc -> EventBusReplier.builder(vertx, rc).array().adress(ApiQuery.FIND).withNoBody());
        return router;
    }

    private Router dependencies() {
        Router router = Router.router(vertx);
        router.get("/").handler(rc -> EventBusReplier.builder(vertx, rc).array().adress(DependencyQuery.FIND).withNoBody());

        router.get("/:resource")
                .handler(rc -> {
                    String resource = rc.request().getParam("resource");
                    if (Objects.isNull(resource) || resource.isEmpty()) {
                        rc.fail(400);
                        return;
                    }
                    EventBusReplier.builder(vertx, rc).array().adress(DependencyQuery.USED_BY).withBody(resource);
                });
        return router;
    }

    /**
     * configuration endpoints.
     *
     * @return the router
     */
    private Router configuration() {
        Router router = Router.router(vertx);
        router.get("/db/export.json").handler(rc ->
                vertx.eventBus().send(BackupQuery.DUMP, new JsonObject(), new DeliveryOptions(), (Handler<AsyncResult<Message<JsonObject>>>) (msg) -> {
                    if (msg.failed()) {
                        rc.response().setStatusCode(500).end();
                        return;
                    }
                    JsonObject body = msg.result().body();
                    new Https.Json(rc).send(body.encodePrettily());
                }));
        router.put("/db/import").handler(rc -> {
            final JsonObject body = rc.getBodyAsJson();
            EventBusReplier.builder(vertx, rc).created().adress(ImportCommand.IMPORT).withBody(body);
        });

        router.put("/db/mysql/schemas").handler(rc -> EventBusReplier.builder(vertx, rc).json().adress(Database.MYSQL_CREATE_SCHEMA).withNoBody());
        router.get("/db/mysql").handler(rc -> EventBusReplier.builder(vertx, rc).array().adress(Database.MYSQL_INFO_SCHEMA).withNoBody());
        router.post("/db/events/store").handler(rc -> EventBusReplier.builder(vertx, rc).json().adress(ArchiveCommand.ARCHIVE).withNoBody());
        router.post("/db/mysql").handler(rc -> EventBusReplier.builder(vertx, rc).json().adress(Database.MYSQL_ON_OFF).withNoBody());
        router.post("/db/mysql/connect").handler(rc -> EventBusReplier.builder(vertx, rc).json().adress(Database.TEST_MYSQL_CONNECTION).withBodyFromRequest());

        router.get("/").handler(
                rc -> EventBusReplier.builder(vertx, rc).json().adress(ConfigurationQuery.GET).withNoBody()
        );
        router.put("/").handler(rc -> EventBusReplier.builder(vertx, rc).created().adress(ConfigurationCommand.SAVE).withBodyFromRequest());
        return router;
    }

    /**
     * TableQuery  endpoints.
     *
     * @return the router
     */
    private Router tables() {
        Router router = Router.router(vertx);
        router.get("/projects").handler(rc -> EventBusReplier.builder(vertx, rc).json().adress(TableQuery.BY_PROJECT).withNoBody());
        router.get("/").handler(rc -> EventBusReplier.builder(vertx, rc).array().adress(TableQuery.LIST).withNoBody());
        return router;
    }

    /**
     * Endpoint for getting raw data.
     *
     * @return the router
     */
    private Router apps() {
        Router router = Router.router(vertx);
        router.post("/").handler(rc -> {
            final JsonObject body = rc.getBodyAsJson()
                    .put("updateState", new Date().getTime());
            EventBusReplier.builder(vertx, rc).created().adress(RawCommand.SAVING).withBody(body);
        });
        router.delete("/").handler(rc -> EventBusReplier.builder(vertx, rc).created().adress(ResetCommand.RUN).withNoBody());
        router.delete("/calculate/datas").handler(rc -> EventBusReplier.builder(vertx, rc).array().adress(NitriteCommand.CLEAR_CALCULATE_DATA).withNoBody());

        return router;
    }

    private Router dump() {
        Router router = Router.router(vertx);
        router.get("/").handler(rc -> EventBusReplier.builder(vertx, rc).json().adress(BackupQuery.DUMP).withNoBody());
        return router;
    }


    /**
     * Endpoint for getting raw data.
     *
     * @return the router
     */
    private Router fronts() {
        Router router = Router.router(vertx);
        router.post("/").handler(rc -> {
            final JsonObject body = rc.getBodyAsJson()
                    .put("updateState", new Date().getTime());

            EventBusReplier.builder(vertx, rc).created().adress(FrontCommand.SAVING).withBody(body);
        });

        router.get("/").handler(rc -> EventBusReplier.builder(vertx, rc).array().adress(FrontAppsQuery.FIND_ALL).withNoBody());
        return router;
    }


    /**
     * ProjectQuery api
     *
     * @return router
     */
    private Router projects() {
        Router router = Router.router(vertx);
        router.get("/:id").handler(rc -> {
            String id = rc.request().params().get("id");
            EventBusReplier.builder(vertx, rc).array().adress(ProjectQuery.GET).withBody(new JsonObject().put(ProjectQuery.ID, id));
        });
        router.get("/").handler(rc -> EventBusReplier.builder(vertx, rc).array().adress(ProjectQuery.LIST).withNoBody());
        return router;
    }
}
