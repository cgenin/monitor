package net.christophe.genin.monitor.domain.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;

@RunWith(VertxUnitRunner.class)
public class HttpTest {


    Vertx vertx;
    private int port;

    @Before
    public void before(TestContext context) throws IOException {
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();


        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("server-port", port));

        vertx = Vertx.vertx();
        vertx.deployVerticle(Http.class.getName(), options, context.asyncAssertSuccess());
    }


    @After
    public void after(TestContext context) {
       // vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void should_read_static_files(TestContext context) {
        Async async = context.async();
        HttpClient client = vertx.createHttpClient();
        client.getNow(port, "localhost", "/", response -> {
            response.bodyHandler(body -> {
                context.assertTrue( body.toString().contains("<body>"));
                client.close();
                async.complete();
            });
        });

    }
}
