package net.christophe.genin.domain.server.model;


import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.adapter.mysql.MysqlVersion.MysqlVersionHandler;
import net.christophe.genin.domain.server.adapter.nitrite.NitriteVersion.NitriteVersionHandler;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import rx.Single;

import java.util.List;

public abstract class Version {

    private JsonObject json;
    private long latestUpdate;
    private final String name;
    private final String idProject;

    public static Single<Version> findByNameAndProjectOrDefault(String version, String idProject) {
        return (Mysqls.Instance.get().active()) ?
                new MysqlVersionHandler(Mysqls.Instance.get()).findByNameAndProjectOrDefault(version, idProject) :
                new NitriteVersionHandler(Dbs.instance).findByNameAndProjectOrDefault(version, idProject);
    }


    public static Single<Integer> removeAll() {
        return (Mysqls.Instance.get().active()) ?
                new MysqlVersionHandler(Mysqls.Instance.get()).removeAll():
                new NitriteVersionHandler(Dbs.instance).removeAll();
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

}
