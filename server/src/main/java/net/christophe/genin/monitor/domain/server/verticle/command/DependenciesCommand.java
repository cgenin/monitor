package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.model.Configuration;
import net.christophe.genin.monitor.domain.server.model.Dependency;
import net.christophe.genin.monitor.domain.server.model.Project;
import net.christophe.genin.monitor.domain.server.model.Raw;
import rx.Observable;

import java.util.Arrays;
import java.util.regex.Pattern;


public class DependenciesCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(DependenciesCommand.class);

    @Override
    public void start() {
        new Periodic(this, logger).run(this::periodic);
    }


    private synchronized Observable<String> periodic() {
        IRaw iRaw = Adapters.get().rawHandler();

        return new RawDomain(iRaw).findByStateFirst(Treatments.DEPENDENCIES)
                .flatMap(doc -> {
                    final String artifactId = doc.artifactId();
                    String usedBy = new DependenciesSanitizer(artifactId).run();

                    return Configuration.load()
                            .toObservable()
                            .flatMap(configuration -> new DependencyDomain(iDependency).save(artifactId,  iProject))
                            .doOnCompleted(
                                    () -> doc.updateState(Treatments.END)
                                            .subscribe(
                                                    bool -> logger.info("dependencies for " + artifactId + " was updated to next: " + bool)
                                            )
                            );
                })
                ;

    }


}
