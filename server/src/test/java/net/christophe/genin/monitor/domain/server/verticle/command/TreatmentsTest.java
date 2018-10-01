package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.Context;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.boundedcontext.domain.VersionDomain;
import net.christophe.genin.monitor.domain.server.verticle.Console;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(VertxUnitRunner.class)
public class TreatmentsTest {


    private static final Logger logger = LoggerFactory.getLogger(TreatmentsTest.class);


    Vertx vertx;
    private AbstractVerticle verticle;


    @Before
    public void before(TestContext context) {
        vertx = Vertx.vertx();
        verticle = new AbstractVerticle();
        Context ctxt = mock(Context.class);
        when(ctxt.config()).thenReturn(new JsonObject());
        verticle.init(vertx.getDelegate(), ctxt);
    }


    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void should_launch_periodic_treatments(TestContext context) {
        Async async = context.async(2);

        vertx.eventBus().<String>consumer(Console.INFO, msg -> {
            context.assertEquals("test OK", msg.body());
            async.countDown();
        });

        new Periodic(verticle, logger).run(() -> {
            async.countDown();
            return Observable.just("test OK");
        });
    }
}
