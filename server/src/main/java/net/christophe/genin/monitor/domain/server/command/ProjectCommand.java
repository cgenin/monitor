package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.command.util.Raws;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.model.Configuration;
import net.christophe.genin.monitor.domain.server.model.Project;
import net.christophe.genin.monitor.domain.server.model.Raw;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ProjectCommand.class);

    @Override
    public void start() {
        new Treatments.Periodic(this, logger).run(this::periodic);
    }

    private synchronized Observable<String> periodic() {

        return Raw.findByStateFirst(Treatments.PROJECTS)
                .flatMap(run());

    }

    Func1<Raw, Observable<String>> run() {
        return doc -> {
            final JsonObject json = doc.json();
            String artifactId = doc.artifactId();
            Long update = doc.update();


            return Configuration.load()
                    .flatMap(conf ->
                            Project.findByName(artifactId)
                                    .flatMap(project -> {
                                        if (project.latestUpdate() < update) {
                                            final List<String> allDeps =
                                                    Raws.extractJavaDeps(json);

                                            List<String> javaFilters = conf.javaFilters();
                                            logger.debug("javaFilters:" + javaFilters);
                                            final List<String> javaDeps = allDeps.parallelStream()
                                                    .map(String::toUpperCase)
                                                    .filter(s ->
                                                            javaFilters.isEmpty() ||
                                                                    javaFilters.parallelStream()
                                                                            .map(String::toUpperCase)
                                                                            .anyMatch(s::contains)
                                                    ).collect(Collectors.toList());
                                            logger.debug("javaDeps:" + javaDeps);
                                            return updateProjectWith(project, artifactId, json, update, javaDeps)
                                                    .save()
                                                    .map(bool -> {
                                                        if (bool) {
                                                            return "Project '" + artifactId + "' updated";
                                                        }
                                                        return "Project '" + artifactId + "' not updated";
                                                    })
                                                    .flatMap(str -> changState(doc, str));
                                        }
                                        String message = "No data for " + artifactId + ". Document must not be updated: " + project.latestUpdate() + " > " + update;
                                        logger.info(message);
                                        return changState(doc, message);
                                    }))
                    .toObservable();
        };
    }

    private Single<String> changState(Raw doc, String message) {
        return doc.updateState(Treatments.TABLES)
                .map(b -> message);
    }

    private Project updateProjectWith(Project project, String artifactId, JsonObject json, long update, List<String> javaDeps) {
        project.setName(artifactId);
        final String version = json.getString(Schemas.Raw.version.name());
        if (Raws.isSnapshot(version)) {
            project.setSnapshot(version);
        } else {
            project.setRelease(version);
        }
        final List<String> tables = Raws.extractTables(json);
        project.setTables(tables);
        project.setJavaDeps(javaDeps);
        Optional.ofNullable(json.getString(Schemas.Projects.changelog.name()))
                .ifPresent(project::setChangeLog);
        final List<String> apis = Raws.extractUrls(json);
        project.setApis(apis);
        logger.info("New data for " + artifactId + ". Document must be updated.");
        project.setLatestUpdate(update);
        return project;
    }


}
