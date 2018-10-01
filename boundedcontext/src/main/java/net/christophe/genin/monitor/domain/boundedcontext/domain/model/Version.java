package net.christophe.genin.monitor.domain.boundedcontext.domain.model;


import io.vertx.core.json.JsonObject;
import rx.Single;

import java.util.List;

/**
 * Model which represents an version for an specific domain.
 */
public abstract class Version {

    private JsonObject json;
    private long latestUpdate;
    private final String name;
    private final String idProject;



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
