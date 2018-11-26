package net.christophe.genin.domain.monitor.addon.verticles;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class PdfVerticleTest {

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    @Before
    public void before(TestContext context) {
        Vertx vertx = rule.vertx();
        vertx.deployVerticle(new PdfVerticle(), context.asyncAssertSuccess());
    }

    @Test
    public void should_generate_pdf_with_html(TestContext context) {
        Async async = context.async();
        Vertx vertx = rule.vertx();
        vertx.eventBus().<Buffer>send(PdfVerticle.HTML_TO_PDF, "<div><strong>TEST</strong></div>", m -> {
            context.assertTrue(m.succeeded());
            Buffer body = m.result().body();
            context.assertNotNull(body);
            context.assertTrue(body.length() > 0);
            async.countDown();
        });
    }

    @Test
    public void should_fail_with_incorrect_html(TestContext context) {
        Async async = context.async();
        Vertx vertx = rule.vertx();
        vertx.eventBus().<Buffer>send(PdfVerticle.HTML_TO_PDF, "<div>dsqdsqd", m -> {
            context.assertFalse(m.succeeded());
            async.countDown();
        });
    }
}
