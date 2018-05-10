package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.Console;
import net.christophe.genin.domain.server.model.Configuration;
import net.christophe.genin.domain.server.model.Dependency;
import net.christophe.genin.domain.server.model.Project;
import net.christophe.genin.domain.server.model.Raw;
import rx.Observable;

import java.util.Arrays;
import java.util.regex.Pattern;


public class DependenciesCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(DependenciesCommand.class);

    @Override
    public void start() {
        new Treatments.Periodic(this, logger).run(this::periodic);
    }


    private synchronized boolean periodic() {

        Raw.findByStateFirst(Treatments.DEPENDENCIES)
                .flatMap(doc -> {
                    final String artifactId = doc.artifactId();
                    String usedBy = new DependenciesSanitizer(artifactId).run();

                    return Configuration.load()
                            .toObservable()
                            .flatMap(configuration -> Dependency.removeByUsedBy(usedBy)
                                    .doOnSuccess((nb) -> logger.debug("Delete : " + nb))
                                    .flatMap(nb -> Project.findByName(artifactId))
                                    .toObservable()
                                    .flatMap(project -> Observable.from(project.javaDeps()))
                                    .map(str -> new DependenciesSanitizer(str).run())
                                    .filter(resource -> !"STARTER".equals(resource))
                                    .distinct()
                                    .flatMap(resource -> Dependency.create(resource, usedBy)
                                            .map(res -> " resource '" + resource +
                                                    "' used by '" + usedBy +
                                                    "' : " + res)
                                            .toObservable()
                                    ))
                            .doOnCompleted(
                                    () -> doc.updateState(Treatments.END)
                                            .subscribe(
                                                    bool -> logger.info("dependencies for " + artifactId + " was updated to next: " + bool)
                                            )
                            );
                })
                .subscribe(
                        str -> {
                            logger.info(str);
                            vertx.eventBus().send(Console.INFO, str);
                        },
                        err -> {
                            logger.error("error in tables for ", err);
                        });

        return true;
    }

    public static class DependenciesSanitizer {

        private static final Pattern[] PATTERNS = new Pattern[]{
                Pattern.compile("-MANAGER"),
                Pattern.compile("-SERVICE"),
                Pattern.compile("-IMPL"),
                Pattern.compile("-CLIENT"),
        };
        private final String str;

        public DependenciesSanitizer(String str) {
            this.str = str;
        }

        private String innerRun(String chaine, Pattern[] regexps) {
            if (regexps.length == 0) {
                return chaine;
            }
            String newChaine = regexps[0].matcher(chaine).replaceAll("");
            Pattern[] newPatterns = (regexps.length == 1) ? new Pattern[0] : Arrays.copyOfRange(regexps, 1, regexps.length);
            return innerRun(newChaine, newPatterns);

        }

        public String run() {
            String US = str.toUpperCase();
            return innerRun(US, PATTERNS);
        }
    }
}
