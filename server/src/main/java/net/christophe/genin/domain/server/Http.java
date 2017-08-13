package net.christophe.genin.domain.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import net.christophe.genin.domain.server.http.Services;

public class Http extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Http.class);

    @Override
    public void start() throws Exception {
        final int port = config().getInteger("port", 8888);
        final String host = config().getString("host", "localhost");

        final HttpServer httpServer = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.mountSubRouter("/api", new Services(vertx).build());

        router.route("/*")
                .handler(StaticHandler.create("build")
                        .setCachingEnabled(true)
                        .setFilesReadOnly(true)
                );

        router.route("/*")
                .handler(StaticHandler.create("build")
//                        .setCachingEnabled(true)
//                        .setMaxAgeSeconds(60 * 60 * 24 * 7 * 4)
                        .setFilesReadOnly(true)
                );

        logger.info("port : " + port);
        logger.info("host : " + host);

        httpServer.requestHandler(router::accept)
                .listen(port, host);
        logger.info("Http server launched !");
    }


}
