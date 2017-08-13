package net.christophe.genin.domain.server.http;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import net.christophe.genin.domain.server.InitializeDb;
import net.christophe.genin.domain.server.Projects;
import net.christophe.genin.domain.server.apps.Raw;

import java.util.Date;

public class Services {

    private final Vertx  vertx;

    public Services(Vertx vertx) {
        this.vertx = vertx;
    }


    public Router build() {
        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Content-Type"));
        router.get("/_health").handler(rc ->
                new Https.EbCaller(vertx, rc).arrAndReply(InitializeDb.HEALTH)
        );
        router.mountSubRouter("/projects", projects());
        router.mountSubRouter("/apps", apps());
        return router;
    }

    private Router apps() {
        Router router = Router.router(vertx);
        router.post("/").handler(rc -> {
            final JsonObject body = rc.getBodyAsJson()
                    .put("update", new Date().getTime());
            vertx.eventBus()
                    .send(Raw.SAVING, body, new DeliveryOptions(), (reply) -> {
                        final Integer status = Https.toStatusCreated(reply);
                        rc.response().setStatusCode(status).end();
                    });
        });
        return router;
    }

    private Router projects() {
        Router router = Router.router(vertx);
        router.get("/").handler(rc -> new Https.EbCaller(vertx, rc).arrAndReply(Projects.LIST));
        return router;
    }
}
