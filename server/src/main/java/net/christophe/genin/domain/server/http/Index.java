package net.christophe.genin.domain.server.http;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import net.christophe.genin.domain.server.Http;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Endpoint for registering the index.html.
 */
public class Index {
    private static final Logger logger = LoggerFactory.getLogger(Http.class);

    /**
     * The paths of routes
     */
    private enum Path {
        index("/"),
        projects("/projects", "/projects/*"),
        apps("/apps", "/apps/*"),
        tables("/tables", "/tables/*"),
        configuration("/configuration"),
        about("/about");

        private final String[] paths;

        Path(String... paths) {
            this.paths = paths;
        }

        public Stream<String> stream() {
            return Arrays.stream(paths);
        }
    }

    /**
     * Register all handlers for index.html.
     *
     * @param router the main router.
     */
    public void register(Router router) {
        final Handler<RoutingContext> indexHandler = indexHandler();
        Arrays.stream(Path.values())
                .flatMap(Path::stream)
                .forEach(p -> router.get(p).handler(indexHandler));
    }

    /**
     * load the file.
     *
     * @param path the classpath path
     * @return the Observable of stream
     */
    private Observable<String> getObservableClasspathResource(String path) {
        return Observable.fromCallable(
                () -> Index.class.getClassLoader().getResourceAsStream(path)
        )
                .map(
                        input -> {
                            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
                                return buffer.lines().collect(Collectors.joining(""));
                            } catch (IOException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                )
                .subscribeOn(Schedulers.io());
    }

    /**
     * Handler http factory.
     *
     * @return an instance of the handler.
     */
    private Handler<RoutingContext> indexHandler() {
        return rc ->
                getObservableClasspathResource("build/index.html")
                        .subscribe(
                                (s) -> rc.response()
                                        .setStatusCode(200)
                                        .putHeader(HttpHeaders.CONTENT_TYPE, "text/html;charset=UTF-8")
                                        .putHeader(HttpHeaders.CACHE_CONTROL, "private, no-cache")
                                        .end(s),
                                (e) -> {
                                    logger.error("Error in getting html page", e);
                                    rc.response()
                                            .setStatusCode(500)
                                            .end();
                                }
                        );
    }

}
