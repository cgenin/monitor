package net.christophe.genin.domain.server.http;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import net.christophe.genin.domain.server.InitializeDb;
import net.christophe.genin.domain.server.command.ConfigurationCommand;
import net.christophe.genin.domain.server.command.Import;
import net.christophe.genin.domain.server.command.Reset;
import net.christophe.genin.domain.server.query.Configuration;
import net.christophe.genin.domain.server.query.Projects;
import net.christophe.genin.domain.server.command.Raw;
import net.christophe.genin.domain.server.query.Tables;

import java.util.Date;

/**
 * Api rest builder.
 */
public class Services {

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
                new Https.EbCaller(vertx, rc).arrAndReply(InitializeDb.HEALTH)
        );
        router.mountSubRouter("/projects", projects());
        router.mountSubRouter("/tables", tables());
        router.mountSubRouter("/endpoints", apis());
        router.mountSubRouter("/configuration", configuration());
        router.mountSubRouter("/apps", apps());
        return router;
    }

    /**
     * Apis  endpoints.
     *
     * @return the router
     */
    private Router apis() {
        Router router = Router.router(vertx);
        router.get("/").handler(rc -> {
            JsonArray arr = new JsonArray();
            arr.add(new JsonObject()
                    .put("name", "create")
                    .put("method", "POST")
                    .put("url", "/api/mon-item")
                    .put("comment", "dqsd dqsdqs dsdqsd ddd ")
                    .put("groupId", "fr.mm.dsds")
                    .put("artifactId", "test-service-impl")
                    .put("latestUpdate", new Date().getTime())
                    .put("since", "1.0.0-SNAPSHOT")
                    .put("className", "IServiceTruc")
            );
            arr.add(new JsonObject()
                    .put("name", "find")
                    .put("method", "GET")
                    .put("url", "/api/mon-item")
                    .put("comment", "dqsd dqsdqs dsdqsd ddd ")
                    .put("groupId", "fr.mm.dsds")
                    .put("artifactId", "test-service-impl")
                    .put("latestUpdate", new Date().getTime())
                    .put("since", "1.0.0-SNAPSHOT")
                    .put("className", "IServiceTruc")
            );
            new Https.Json(rc).send(arr);
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
            new Https.EbCaller(vertx, rc).created(Import.IMPORT, body);
        });
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
