package net.christophe.genin.monitor.domain.boundedcontext.domain;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IProject;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Configuration;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Project;
import net.christophe.genin.monitor.domain.boundedcontext.domain.utils.Raws;
import net.christophe.genin.monitor.domain.boundedcontext.domain.utils.Schemas;
import rx.Observable;
import rx.Single;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProjectDomain {
    private static final Logger logger = LoggerFactory.getLogger(ProjectDomain.class);

    private final IProject projectDao;

    public ProjectDomain(IProject projectDao) {
        this.projectDao = projectDao;
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


    public Single<String> saveOrUpdate(JsonObject json, String artifactId, Long update, Function<String, Single<String>> next, Configuration conf) {
        return projectDao.readByName(artifactId)
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
                                .flatMap(next::apply);
                    }
                    String message = "No data for " + artifactId + ". Document must not be updated: " + project.latestUpdate() + " > " + update;
                    logger.info(message);
                    return next.apply(message);
                });
    }


    public Observable<JsonArray> list() {
        return projectDao.findAll()
                .map(project -> {
                    final JsonObject obj = new JsonObject();
                    Optional.ofNullable(project.release())
                            .ifPresent(s -> obj.put(Schemas.Projects.release.name(), s));
                    Optional.ofNullable(project.snapshot())
                            .ifPresent((s) -> obj.put(Schemas.Projects.snapshot.name(), s));
                    return obj
                            .put(Schemas.Projects.id.name(), project.id())
                            .put(Schemas.Projects.name.name(), project.name())
                            .put(Schemas.Projects.latestUpdate.name(), project.latestUpdate())
                            .put(Schemas.Projects.tables.name(), new JsonArray(project.tables()))
                            .put(Schemas.Projects.apis.name(), new JsonArray(project.apis()))
                            .put(Schemas.Projects.changelog.name(), project.changelog())
                            .put(Schemas.Projects.javaDeps.name(), new JsonArray(project.javaDeps()));
                })
                .reduce(new JsonArray(), JsonArray::add);
    }

    public Single<Integer> removeAll() {
        return projectDao.removeAll();
    }
}
