package net.christophe.genin.monitor.domain.server.model;

import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import rx.Observable;
import rx.Single;

import java.util.HashMap;
import java.util.Set;

public abstract class Table {
    private String id;
    private String Service;
    private String tableName;
    private long lastUpdated;


    public static Observable<Set<String>> findByService(String artifactId) {
        return Adapters.get().tableHandler().findByService(artifactId);
    }

    public static Single<Boolean> remove(String tableName, String artifactId) {
        return Adapters.get().tableHandler().remove(tableName, artifactId)  ;
    }

    public static Table newInstance() {
        return Adapters.get().tableHandler().newInstance();
    }

    public static Single<Integer> removeAll() {
        return Adapters.get().tableHandler().removeAll();
    }

    public static Observable<Table> findAll() {
        return Adapters.get().tableHandler().findAll();
    }

    public static Observable<HashMap<String, Long>> countTablesByProjects() {
        return Adapters.get().tableHandler().countTablesByProjects();
    }

    public String id() {
        return id;
    }

    public Table setId(String id) {
        this.id = id;
        return this;
    }

    public String service() {
        return Service;
    }

    public Table setService(String service) {
        Service = service;
        return this;
    }

    public String tableName() {
        return tableName;
    }

    public Table setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public long lastUpdated() {
        return lastUpdated;
    }

    public Table setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public abstract Single<Boolean> create();
}
