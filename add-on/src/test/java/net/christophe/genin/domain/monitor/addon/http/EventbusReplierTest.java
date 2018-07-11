package net.christophe.genin.domain.monitor.addon.http;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

@RunWith(VertxUnitRunner.class)
public class EventbusReplierTest {

    private Vertx vertx;
    private RoutingContext routingContext;
    private HttpServerResponse response;

    @Before
    public void before(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(TestVerticle.class.getName(), context.asyncAssertSuccess());
        routingContext = mock(RoutingContext.class);
        response = mock(HttpServerResponse.class);
        when(response.setStatusCode(anyInt())).thenReturn(response);
        when(response.putHeader(anyString(), anyString())).thenReturn(response);
        when(response.putHeader(any(CharSequence.class), any(CharSequence.class))).thenReturn(response);
        when(routingContext.response()).thenReturn(response);
    }

    @Test
    public void should_send_jsonAndReply(TestContext context) {
        Async async = context.async();
        EventBusReplier.builder(vertx, routingContext)
                .json()
                .adress(TestVerticle.JSON)
                .handle(defaultTest200(context, async))
                .withNoBody();

    }

    @Test
    public void should_send_jsonAndReply_500_when_failed(TestContext context) {
        Async async = context.async();
        EventBusReplier.builder(vertx, routingContext)
                .json()
                .adress(TestVerticle.ERROR)
                .handle(res -> {
                    context.assertFalse(res);
                    verify(response, times(1)).end();
                    context.assertEquals(Https.ERROR_STATUS, extractStatus());
                    async.countDown();
                })
                .withNoBody();
    }

    @Test
    public void should_send_jsonAndReply_with_body(TestContext context) {
        Async async = context.async();
        EventBusReplier.builder(vertx, routingContext)
                .json()
                .adress(TestVerticle.JSON)
                .handle(defaultTest200(context, async))
                .withBody(new JsonObject().put("an", "body"));

    }

    @Test
    public void should_send_arrAndReply(TestContext context) {
        Async async = context.async();
        EventBusReplier.builder(vertx, routingContext)
                .array()
                .adress(TestVerticle.ARRAY)
                .handle(res -> {
                    context.assertTrue(res);
                    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
                    verify(response, times(1)).end(captor.capture());
                    context.assertEquals("[{\"test\":true}]", captor.getValue());
                    context.assertEquals(200, extractStatus());
                    async.countDown();
                })
                .withNoBody();

    }

    private Handler<Boolean> defaultTest200(TestContext context, Async async) {
        return res -> {
            context.assertTrue(res);
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(response, times(1)).end(captor.capture());
            context.assertEquals("{\"test\":true}", captor.getValue());
            context.assertEquals(200, extractStatus());
            async.countDown();
        };
    }

    private Integer extractStatus() {
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(response, times(1)).setStatusCode(captor.capture());
        return captor.getValue();
    }

    @Test
    public void should_send_created(TestContext context) {
        Async async = context.async();
        EventBusReplier.builder(vertx, routingContext).created()
                .adress(TestVerticle.JSON)
                .handle(res -> {
                    context.assertTrue(res);

                    verify(response, times(1)).end();
                    context.assertEquals(Https.CREATED_STATUS, extractStatus());

                    async.countDown();
                }).withBody(new JsonObject().put("created", true));

    }

    @Test
    public void should_send_created_500_when_failed(TestContext context) {
        Async async = context.async();
        EventBusReplier.builder(vertx, routingContext)
                .created()
                .adress(TestVerticle.ERROR)
                .handle(res -> {
                    context.assertFalse(res);
                    verify(response, times(1)).end();
                    context.assertEquals(Https.ERROR_STATUS, extractStatus());
                    async.countDown();
                })
                .withBody(new JsonObject().put("created", true));
    }
}
