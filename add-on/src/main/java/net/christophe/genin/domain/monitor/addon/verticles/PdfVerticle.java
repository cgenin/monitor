package net.christophe.genin.domain.monitor.addon.verticles;


import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.Message;
import org.xhtmlrenderer.pdf.ITextRenderer;
import rx.Single;
import rx.schedulers.Schedulers;

import java.io.ByteArrayOutputStream;
import java.util.Optional;

/**
 * Verticle builder to create PDF.
 */
public class PdfVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MustacheVerticle.class);
    public static final String COMPILE = PdfVerticle.class.getName() + ".compile";
    public static final String HTML_TO_PDF = PdfVerticle.class.getName() + ".html.to.pdf";


    private Optional<MustacheVerticleBuilder> opMvb = Optional.empty();
    private Optional<PdfVerticleBuilder> opPvb = Optional.empty();

    public PdfVerticle() {
    }

    public PdfVerticle(MustacheVerticleBuilder mvb, PdfVerticleBuilder pvb) {
        this.opMvb = Optional.of(mvb);
        this.opPvb = Optional.of(pvb);
    }

    @Override
    public void start(Future<Void> startFuture) {

        AbstractVerticle mustacheVerticle = opMvb.map(MustacheVerticleBuilder::build).orElse(new MustacheVerticle());
        vertx.getDelegate().deployVerticle(mustacheVerticle, m -> {
            if (m.succeeded()) {
                logger.info("mustacheVerticle is started");
                launcheEventBusServices(startFuture);
                return;
            }

            logger.error("Error in starting", m.cause());
            startFuture.fail(m.cause());
        });
    }

    /**
     * Method for generating PDF.
     *
     * @param msg    the event bus message
     * @param single the single.
     * @param <T>    The type of the message
     */
    private <T> void generatePdf(Message<T> msg, Single<String> single) {
        single
                .subscribeOn(Schedulers.computation())
                .map(text -> {
                    try {
                        final ITextRenderer renderer = new ITextRenderer();
                        renderer.setDocumentFromString(text);
                        renderer.layout();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        renderer.createPDF(byteArrayOutputStream, true, 1);
                        return byteArrayOutputStream.toByteArray();
                    } catch (Exception e) {
                        throw new IllegalStateException("Error in generating pdf for " + msg.body(), e);
                    }
                })
                .map(Buffer::buffer)
                .subscribe(
                        msg::reply,
                        err -> {
                            logger.error("error", err);
                            msg.fail(500, "error");
                        }
                );

    }

    /**
     * Create Handlers for the verticle.
     *
     * @param startFuture the future.
     */
    private void launcheEventBusServices(Future<Void> startFuture) {
        vertx.eventBus().<JsonObject>consumer(COMPILE, msg ->
                generatePdf(
                        msg,
                        Single.just(msg.body())
                                .flatMap(body -> vertx.eventBus().<String>rxSend(MustacheVerticle.COMPILE, body))
                                .map(Message::body)
                )
        );

        vertx.eventBus().<String>consumer(HTML_TO_PDF, msg ->
                generatePdf(
                        msg,
                        Single.just(msg.body())
                )
        );

        startFuture.complete();
    }
}
