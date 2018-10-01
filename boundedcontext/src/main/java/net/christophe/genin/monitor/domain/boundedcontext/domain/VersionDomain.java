package net.christophe.genin.monitor.domain.boundedcontext.domain;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IProject;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IVersion;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Project;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Version;
import net.christophe.genin.monitor.domain.boundedcontext.domain.utils.Raws;
import net.christophe.genin.monitor.domain.boundedcontext.domain.utils.Schemas;
import rx.Observable;
import rx.Single;

import java.util.List;
import java.util.Optional;

public class VersionDomain {

    private final IVersion versionDao;

    public VersionDomain(IVersion versionDao) {
        this.versionDao = versionDao;
    }

    public Single<Version> findByNameAndProjectOrDefault(String version, String idProject) {
        return versionDao.findByNameAndProjectOrDefault(version, idProject);
    }


    public Observable<String> saveOrUpdate(IProject projectDao, JsonObject json, String artifactId, Long update, String version) {
        return projectDao.readByName(artifactId)
                .map(Project::id)
                .flatMap(idProject -> this.findByNameAndProjectOrDefault(version, idProject))
                .toObservable()
                .flatMap(currentDoc -> {
                    long lDate = currentDoc.latestUpdate();
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
                        updateResult);
    }

    public  Single<Integer> removeAll() {
        return versionDao.removeAll();
    }


    public  Observable<JsonArray> findByProject(String idProject) {

        return versionDao.findByProject(idProject)
                .map(version -> new JsonObject()
                        .put(Schemas.Version.id.name(), version.id())
                        .put(Schemas.Version.name.name(), version.name())
                        .put(Schemas.Version.isSnapshot.name(), version.isSnapshot())
                        .put(Schemas.Version.changelog.name(), version.changelog())
                        .put(Schemas.Version.latestUpdate.name(), version.latestUpdate())
                        .put(Schemas.Version.tables.name(), new JsonArray(version.tables()))
                        .put(Schemas.Version.apis.name(), new JsonArray(version.apis()))
                        .put(Schemas.Version.javaDeps.name(), new JsonArray(version.javaDeps())))
                .reduce(new JsonArray(), JsonArray::add);
    }
    public  Observable<Version> findAll() {
        return versionDao.findAll();
    }


    public Observable<JsonArray> extract(){
        return this.findAll()
                .map(version -> new JsonObject()
                                .put("ID", version.id())
                                .put("NAME", version.name())
                                .put("IDPROJECT", version.idProject())
                        // TODO
                )
                .reduce(new JsonArray(), JsonArray::add);
    }


}
