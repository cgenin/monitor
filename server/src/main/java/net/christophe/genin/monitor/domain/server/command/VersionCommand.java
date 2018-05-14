package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.Console;
import net.christophe.genin.monitor.domain.server.command.util.Raws;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.model.Project;
import net.christophe.genin.monitor.domain.server.model.Raw;
import net.christophe.genin.monitor.domain.server.model.Version;
import rx.Observable;

import java.util.List;
import java.util.Optional;

public class VersionCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(VersionCommand.class);

    @Override
    public void start() {
        new Treatments.Periodic(this, logger).run(this::periodic);
    }


    private synchronized boolean periodic() {
        Raw.findByStateFirst(Treatments.VERSION)
                .flatMap(raw -> {
                    final JsonObject json = raw.json();
                    final String artifactId = raw.artifactId();
                    final String version = json.getString(Schemas.Raw.version.name());
                    return Project.findByName(artifactId)
                            .map(Project::id)
                            .flatMap(idProject -> Version.findByNameAndProjectOrDefault(version, idProject))
                            .toObservable()
                            .flatMap(currentDoc -> {
                                long lDate = currentDoc.latestUpdate();
                                long update = json.getLong(Schemas.Raw.update.name());
                                if (lDate < update) {
                                    boolean snapshot = Raws.isSnapshot(version);
                                    List<String> javaDeps = Raws.extractJavaDeps(json);
                                    List<String> tables = Raws.extractTables(json);
                                    List<String> urls = Raws.extractUrls(json);
                                    Optional.ofNullable(json.getString(Schemas.Projects.changelog.name()))
                                            .ifPresent(currentDoc::setChangeLog);
                                    return Observable.just(currentDoc
                                            .setIsSnapshot(snapshot)
                                            .setJavadeps(javaDeps)
                                            .setTables(tables)
                                            .setApis(urls));
                                }
                                return Observable.empty();
                            })
                            .flatMap(document -> document.save()
                                    .toObservable())
                            .map(updateResult -> "Version '" + version +
                                    "' for artifact '" + artifactId + "' updated : " +
                                    updateResult)
                            .doOnCompleted(
                                    () -> raw.updateState(Treatments.URL)
                                            .subscribe(bool -> logger.info("Version (" + version + ") for project " + artifactId + " wad updated to next :" + bool))
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


}
