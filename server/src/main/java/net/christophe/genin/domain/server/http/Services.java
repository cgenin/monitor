package net.christophe.genin.domain.server.http;

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
import net.christophe.genin.domain.server.InitializeDb;
import net.christophe.genin.domain.server.command.*;
import net.christophe.genin.domain.server.query.*;

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
        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.PUT)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Content-Type"));
        router.get("/_health").handler(rc ->
                new Https.EbCaller(vertx, rc).jsonAndReply(InitializeDb.HEALTH)
        );
        router.mountSubRouter("/projects", projects());
        router.mountSubRouter("/tables", tables());
        router.mountSubRouter("/endpoints", apis());
        router.mountSubRouter("/dependencies", dependencies());
        router.mountSubRouter("/configuration", configuration());
        router.mountSubRouter("/dump", dump());
        router.mountSubRouter("/apps", apps());
        router.mountSubRouter("/fronts", fronts());
        return router;
    }

    /**
     * Apis  endpoints.
     *
     * @return the router
     */
    private Router apis() {
        Router router = Router.router(vertx);
        router.get("/").handler(rc -> new Https.EbCaller(vertx, rc).arrAndReply(Endpoints.FIND));
        return router;
    }

    private Router dependencies() {
        Router router = Router.router(vertx);
        router.get("/").handler(rc -> new Https.EbCaller(vertx, rc).arrAndReply(Dependencies.FIND));

        router.get("/:resource")
                .handler(rc -> {
                    String resource = rc.request().getParam("resource");
                    if (Objects.isNull(resource) || resource.isEmpty()) {
                        rc.fail(400);
                        return;
                    }
                    new Https.EbCaller(vertx, rc).arrAndReply(Dependencies.USED_BY, resource);
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
                vertx.eventBus().send(Configuration.EXPORTER, new JsonObject(), new DeliveryOptions(), (Handler<AsyncResult<Message<String>>>) (msg) -> {
                    if (msg.failed()) {
                        rc.response().setStatusCode(500).end();
                        return;
                    }
                    new Https.Json(rc).send(msg.result().body());
                }));
        router.put("/db/import").handler(rc -> {
            final JsonObject body = rc.getBodyAsJson();
            new Https.EbCaller(vertx, rc).created(ImportExport.IMPORT, body);
        });

        router.put("/db/mysql/schemas").handler(rc -> new Https.EbCaller(vertx, rc).jsonAndReply(InitializeDb.MYSQL_CREATE_SCHEMA));
        router.post("/db/mysql/export/events").handler(rc -> new Https.EbCaller(vertx, rc).jsonAndReply(ImportExport.EXPORT));
        router.post("/db/mysql").handler(rc -> new Https.EbCaller(vertx, rc).jsonAndReply(InitializeDb.MYSQL_ON_OFF));

        router.get("/").handler(
                rc -> new Https.EbCaller(vertx, rc).jsonAndReply(Configuration.GET)
        );
        router.put("/").handler(rc -> {
            final JsonObject body = rc.getBodyAsJson();
            new Https.EbCaller(vertx, rc).created(ConfigurationCommand.SAVE, body);
        });
        return router;
    }

    /**
     * Tables  endpoints.
     *
     * @return the router
     */
    private Router tables() {
        Router router = Router.router(vertx);
        router.get("/projects").handler(rc -> new Https.EbCaller(vertx, rc).jsonAndReply(Tables.BY_PROJECT));
        router.get("/").handler(rc -> new Https.EbCaller(vertx, rc).arrAndReply(Tables.LIST));
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
                    .put("update", new Date().getTime());
            new Https.EbCaller(vertx, rc).created(Raw.SAVING, body);
        });
        router.delete("/").handler(rc -> new Https.EbCaller(vertx, rc).created(Reset.RUN, new JsonObject()));
        router.delete("/calculate/datas").handler(rc -> new Https.EbCaller(vertx, rc).arrAndReply(Raw.CLEAR_CALCULATE_DATA, new JsonObject()));

        return router;
    }

    private Router dump() {
        Router router = Router.router(vertx);
        router.get("/").handler(rc -> {
            new Https.EbCaller(vertx, rc).jsonAndReply(Backup.DUMP);
        });
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
                    .put("update", new Date().getTime());

            new Https.EbCaller(vertx, rc).created(Front.SAVING, body);
        });


        return router;
    }


    /**
     * Projects api
     *
     * @return router
     */
    private Router projects() {
        Router router = Router.router(vertx);
        router.get("/:id").handler(rc -> {
            String id = rc.request().params().get("id");
            new Https.EbCaller(vertx, rc).arrAndReply(Projects.GET, new JsonObject().put(Projects.ID, id));
        });
        router.get("/").handler(rc -> new Https.EbCaller(vertx, rc).arrAndReply(Projects.LIST));
        return router;
    }
}
