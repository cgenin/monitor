package net.christophe.genin.domain.monitor.addon.verticles;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import net.christophe.genin.domain.monitor.addon.verticles.mustache.ClasspathTemplate;
import net.christophe.genin.domain.monitor.addon.verticles.mustache.FileSystemTemplate;
import net.christophe.genin.domain.monitor.addon.verticles.mustache.KnownTemplate;
import net.christophe.genin.domain.monitor.addon.verticles.mustache.RawTemplate;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MustacheVerticle extends AbstractVerticle {


    private static final Logger logger = LoggerFactory.getLogger(MustacheVerticle.class);
    public static final String REGISTER = MustacheVerticle.class.getName() + ".register";
    public static final String COMPILE = MustacheVerticle.class.getName() + ".compile";
    private static final Pattern PATTERN_CLASSPATH = Pattern.compile("([^/]+)\\.(.+)$");
    private static final Pattern PATTERN_WINDOWS = Pattern.compile("\\\\");

    private final ConcurrentHashMap<String, KnownTemplate> templates = new ConcurrentHashMap<>();
    private final Optional<MustacheVerticleBuilder> builder;

    static String extractNameFromTemplate(String path) {
        Matcher matcher = PATTERN_CLASSPATH.matcher(path);
        if (matcher.find()) {
            String filename = matcher.group(1);
            String extension = matcher.group(2);
            return filename + "." + extension;
        }

        throw new IllegalStateException("Template classpath name not valid :" + path);
    }

    static String extractNameFromSystemTemplate(String path) {
        String tmp = PATTERN_WINDOWS.matcher(path)
                .replaceAll("/");
        Matcher matcher = PATTERN_CLASSPATH.matcher(tmp);
        if (matcher.find()) {
            String filename = matcher.group(1);
            String extension = matcher.group(2);
            return filename + "." + extension;
        }

        throw new IllegalStateException("Template fileSystem name not valid :" + path);
    }


    private static Func1<String, KnownTemplate> loadFromFileSystem(Boolean cache) {
        return pathStr -> {
            String name = extractNameFromSystemTemplate(pathStr);
            return new FileSystemTemplate(name, pathStr, cache).registered();
        };
    }

    public MustacheVerticle() {
        this(Optional.empty());
    }

    MustacheVerticle(MustacheVerticleBuilder builder) {
        this(Optional.of(builder));
    }

    private MustacheVerticle(Optional<MustacheVerticleBuilder> mustacheVerticleBuilder) {
        this.builder = mustacheVerticleBuilder;
    }


    @Override
    public void start(Future<Void> startFuture) {
        Boolean cache = builder.map(b -> b.cache).orElse(true);
        registerTemplatesOnStarting(cache, v -> {
            vertx.eventBus().<JsonObject>consumer(REGISTER, (msg) -> {
                JsonObject body = msg.body();
                String name = body.getString("name");
                String raw = body.getString("raw");
                RawTemplate rawTemplate = new RawTemplate(name, raw, cache);
                addTemplates().call(templates, rawTemplate);
                logger.info("Register new template : " + name);
                msg.reply(new JsonObject().put("result", true));
            });
            vertx.eventBus().<JsonObject>consumer(COMPILE, (msg) -> {
                JsonObject body = msg.body();
                String name = body.getString("name");
                JsonObject datas = body.getJsonObject("datas");

                Single.just(templates)
                        .subscribeOn(Schedulers.computation())
                        .map(ts -> Objects.requireNonNull(ts.get(name), "Template not found " + name))
                        .map(KnownTemplate::getCompiler)
                        .map(template -> template.execute(datas.getMap()))
                        .subscribe(
                                msg::reply,
                                err -> {
                                    String message = "Error in compiling templates :" + body.encode();
                                    logger.error(message, err);
                                    msg.fail(500, message);
                                });
            });
            startFuture.complete();
        });


    }

    private void registerTemplatesOnStarting(Boolean cache, Handler<Void> handler) {
        Observable<KnownTemplate> declaredTemplates = builder.map(b -> {
            Observable<KnownTemplate> obsClasspath = Observable.from(b.classpathTemplates)
                    .map(templatePath -> {
                        String name = extractNameFromTemplate(templatePath);
                        return new ClasspathTemplate(name, templatePath, cache).registered();
                    });
            Observable<KnownTemplate> obsFileSystem = Observable.from(b.systemfilesTemplates)
                    .subscribeOn(Schedulers.io())
                    .map(loadFromFileSystem(cache));
            Observable<KnownTemplate> obsFolders = Observable.from(b.systemfilesFolders)
                    .subscribeOn(Schedulers.io())
                    .flatMap(templatesFolders -> vertx.fileSystem().rxReadDir(templatesFolders)
                            .toObservable()
                            .flatMap(Observable::from))
                    .map(loadFromFileSystem(cache));


            return Observable.concat(obsClasspath, obsFileSystem, obsFolders)
                    .onErrorResumeNext(err -> {
                        logger.error("error in loading ", err);
                        logger.error("So one template in classpath is skipping");
                        return Observable.empty();
                    });
        })
                .orElse(Observable.empty());


        declaredTemplates
                .reduce(templates, addTemplates())
                .subscribe(
                        m -> {
                            logger.info("Registered " + templates.size() + " on starting.");
                            handler.handle(null);
                        },
                        err -> logger.error("An error during registering op.", err)
                );
    }

    private Func2<ConcurrentHashMap<String, KnownTemplate>, KnownTemplate, ConcurrentHashMap<String, KnownTemplate>> addTemplates() {
        return (acc, template) -> {
            String name = template.getName();
            if (acc.containsKey(name)) {
                logger.warn("Two Templates are named : " + name);
                logger.warn("* 1 : " + acc.get(name).getPath());
                logger.warn("* 2 : " + template.getPath());
                logger.warn("Just the last is stored.");
            }

            acc.put(name, template);
            return acc;
        };
    }
}
