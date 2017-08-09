package net.christophe.genin.domain.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class Http extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Http.class);

    @Override
    public void start() throws Exception {
        final int port = config().getInteger("port", 8888);
        final String host = config().getString("host", "localhost");

        final HttpServer httpServer = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

        final Router router = Router.router(vertx);

        router.mountSubRouter("/api", api());

        router.route("/*")
                .handler(StaticHandler.create("build")
                        .setCachingEnabled(true)
                        .setFilesReadOnly(true)
                );

        logger.info("port : " + port);
        logger.info("host : " + host);

        httpServer.requestHandler(router::accept)
                .listen(port, host);
        logger.info("Http server launched !");
    }

    private Router api() {
        Router router = Router.router(vertx);
        router.get("/_health").handler(rc ->
                vertx.eventBus()
                        .send(Db.HEALTH, new JsonObject(), new DeliveryOptions(), (Handler<AsyncResult<Message<JsonArray>>>) (reply) -> {
                            if (reply.succeeded()) {
                                JsonArray jsonArray = reply.result().body();
                                rc.response().end(jsonArray.encode());
                                return;
                            }
                            logger.error("Error", reply.cause());
                            rc.response().setStatusCode(500).end();
                        })
        );
        return router;
    }
}
