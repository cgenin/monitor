package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.Console;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.ConfigurationDto;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.model.Project;
import net.christophe.genin.domain.server.model.Raw;
import net.christophe.genin.domain.server.query.ConfigurationQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ProjectCommand.class);

    @Override
    public void start() {
        new Treatments.Periodic(this, logger).run(this::periodic);
    }

    private synchronized boolean periodic() {

        Raw.findByStateFirst(Treatments.PROJECTS)
                .flatMap(doc -> {
                    final JsonObject json = doc.json();
                    String artifactId = doc.artifactId();
                    long update = doc.update();

                    return Project.findByName(artifactId)
                            .flatMap(project -> {
                                if (project.latestUpdate() < update) {
                                    project.setName(artifactId);
                                    final String version = json.getString(Schemas.Raw.version.name());
                                    if (Commands.Projects.isSnapshot(version)) {
                                        project.setSnapshot(version);
                                    } else {
                                        project.setRelease(version);
                                    }


                                    final List<String> tables = Commands.Projects.extractTables(json);
                                    project.setTables(tables);
                                    final List<String> allDeps = Commands.Projects.extractJavaDeps(json);
                                    final ConfigurationDto conf = ConfigurationQuery.get();

                                    List<String> javaFilters = conf.getJavaFilters();
                                    final List<String> javaDeps = allDeps.parallelStream()
                                            .map(String::toUpperCase)
                                            .filter(s ->
                                                    javaFilters.isEmpty() ||
                                                            javaFilters.parallelStream()
                                                                    .map(String::toUpperCase)
                                                                    .anyMatch(s::contains)
                                            ).collect(Collectors.toList());
                                    project.setJavaDeps(javaDeps);
                                    Optional.ofNullable(json.getString(Schemas.Projects.changelog.name()))
                                            .ifPresent(project::setChangeLog);
                                    final List<String> apis = Commands.Projects.extractUrls(json);
                                    project.setApis(apis);
                                    logger.info("New data for " + artifactId + ". Document must be updated.");
                                    project.setLatestUpdate(update);
                                    return project.save()
                                            .map(bool -> {
                                                if (bool) {
                                                    return "Projects '" + artifactId + "' updated";
                                                }
                                                return "Projects '" + artifactId + "' not updated";
                                            })
                                            .flatMap(str -> {
                                                return doc.updateState(Treatments.TABLES)
                                                        .map(b -> {
                                                            return str;
                                                        });
                                            });
                                }
                                String message = "No data for " + artifactId + ". Document must not be updated: " + project.latestUpdate() + " > " + update;
                                logger.info(message);
                                return doc.updateState(Treatments.TABLES)
                                        .map(b -> message);
                            })
                            .toObservable();
                })
                .subscribe(
                        str -> {
                            logger.info(str);
                            vertx.eventBus().send(Console.INFO, str);
                        },
                        err -> {
                            logger.error("Error in projects batch", err);
                        });

        return true;
    }


}
