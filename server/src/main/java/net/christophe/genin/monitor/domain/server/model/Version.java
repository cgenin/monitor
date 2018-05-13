package net.christophe.genin.monitor.domain.server.model;


import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import rx.Observable;
import rx.Single;

import java.util.List;

public abstract class Version {

    private JsonObject json;
    private long latestUpdate;
    private final String name;
    private final String idProject;

    public static Single<Version> findByNameAndProjectOrDefault(String version, String idProject) {
        return Adapters.get().versionHandler().findByNameAndProjectOrDefault(version, idProject);
    }


    public static Single<Integer> removeAll() {
        return Adapters.get().versionHandler().removeAll();
    }


    public static Observable<Version> findByProject(String idProject) {
        return Adapters.get().versionHandler().findByProject(idProject);
    }
    public static Observable<Version> findAll() {
        return Adapters.get().versionHandler().findAll();
    }

    protected Version(String name, String idProject) {
        this.name = name;
        this.idProject = idProject;
    }



    public JsonObject json() {
        return json;
    }

    public Version setJson(JsonObject json) {
        this.json = json;
        return this;
    }

    public long latestUpdate() {
        return latestUpdate;
    }

    public Version setLatestUpdate(long latestUpdate) {
        this.latestUpdate = latestUpdate;
        return this;
    }

    public String name() {
        return name;
    }


    public String idProject() {
        return idProject;
    }

    public abstract String id();


    public abstract Version setIsSnapshot(boolean snapshot);
    public abstract Version setJavadeps(List<String> javadeps);
    public abstract Version setTables(List<String> tables);
    public abstract Version setChangeLog(String changeLog);
    public abstract Version setApis(List<String> apis);

    public abstract Single<Boolean> save();

    public abstract boolean isSnapshot();

    public abstract String changelog();

    public abstract List<String> tables();

    public abstract List<String> apis();

    public abstract List<String> javaDeps();
}
