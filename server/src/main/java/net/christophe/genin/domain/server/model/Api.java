package net.christophe.genin.domain.server.model;

import net.christophe.genin.domain.server.adapter.mysql.MysqlApi.MysqlApiHandler;
import net.christophe.genin.domain.server.adapter.nitrite.NitriteApi;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import rx.Observable;
import rx.Single;


public abstract class Api {

    protected final String method;
    protected final String path;
    protected final String idProject;

    public static Observable<Integer> deleteByIdProject(String idProject) {
        return (Mysqls.Instance.get().active()) ?
                new MysqlApiHandler(Mysqls.Instance.get()).deleteByIdProject(idProject) :
                NitriteApi.deleteByIdProject(idProject);
    }

    public static Api newInstance(String method, String path, String idProject) {
        return (Mysqls.Instance.get().active()) ?
                new MysqlApiHandler(Mysqls.Instance.get()).newInstance(method, path, idProject) :
                NitriteApi.newInstance(method, path, idProject);

    }

    public static Single<Integer> removeAll() {
        return (Mysqls.Instance.get().active()) ?
                new MysqlApiHandler(Mysqls.Instance.get()).removeAll() :
                NitriteApi.removeAll();
    }

    public Api(String method, String path, String idProject) {
        this.method = method;
        this.path = path;
        this.idProject = idProject;
    }



    public abstract String id();

    public abstract Api setId(String newId);

    public abstract Api setArtifactId(String artifactId);

    public abstract Api setGroupId(String groupId);

    public abstract Api setSince(String version);

    public abstract Api setLatestUpdate(long update);

    public abstract Api setName(String name);

    public abstract Api setReturns(String returns);

    public abstract Api setParams(String params);

    public abstract Api setComment(String comment);

    public abstract Api setClassName(String className);

    public abstract Single<Boolean> create();
}
