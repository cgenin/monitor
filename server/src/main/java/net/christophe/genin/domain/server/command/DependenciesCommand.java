package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.Console;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.model.Configuration;
import net.christophe.genin.domain.server.model.Dependency;
import net.christophe.genin.domain.server.model.Project;
import net.christophe.genin.domain.server.model.Raw;
import rx.Observable;


public class DependenciesCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(DependenciesCommand.class);

    @Override
    public void start() {
        new Treatments.Periodic(this, logger).run(this::periodic);
    }


    private synchronized boolean periodic() {

        Raw.findByStateFirst(Treatments.DEPENDENCIES)
                .flatMap(doc -> {
                    final JsonObject json = doc.json();
                    final String artifactId = doc.artifactId();
                    String usedBy = new Commands.DependenciesSanitizer(artifactId).run();

                    return Configuration.load()
                            .toObservable()
                            .flatMap(configuration -> Dependency.removeByUsedBy(usedBy)
                                    .doOnSuccess((nb) -> logger.debug("Delete : " + nb))
                                    .flatMap(nb -> Project.findByName(artifactId))
                                    .toObservable()
                                    .flatMap(project -> Observable.from(project.javaDeps()))
                                    .map(str -> new Commands.DependenciesSanitizer(str.toString()).run())
                                    .filter(resource -> !"STARTER".equals(resource))
                                    .distinct()
                                    .flatMap(resource -> Dependency.create(resource, usedBy)
                                            .map(res -> " resource '" + resource +
                                                    "' used by '" + usedBy +
                                                    "' : " + res)
                                            .toObservable()
                                    ));
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


}
