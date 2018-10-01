package net.christophe.genin.monitor.domain.server.model;

import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import rx.Observable;
import rx.Single;

/**
 * Class Model which represent an Service Rest.
 *
 */
public abstract class Api {

    protected final String method;
    protected final String path;
    protected final String idProject;



    public Api(String method, String path, String idProject) {
        this.method = method;
        this.path = path;
        this.idProject = idProject;
    }

    public String method(){
        return method;
    }

    public String path(){
        return path;
    }

    public String idProject(){
        return idProject;
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

    public abstract String name();

    public abstract String artifactId();

    public abstract String groupId();

    public abstract String returns();

    public abstract String params();

    public abstract String comment();


    public abstract String since();


    public abstract String className();


    public abstract long latestUpdate();
}
