package net.christophe.genin.domain.server.model;

import net.christophe.genin.domain.server.adapter.mysql.MysqlTable.MysqlTableHandler;
import net.christophe.genin.domain.server.adapter.nitrite.NitriteTable.NitriteTableHandler;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import rx.Observable;
import rx.Single;

import java.util.Set;

public abstract class Table {
    private String id;
    private String Service;
    private String tableName;
    private long lastUpdated;


    public static Observable<Set<String>> findByService(String artifactId) {
        return (Mysqls.Instance.get().active()) ?
                new MysqlTableHandler(Mysqls.Instance.get()).findByService(artifactId) :
                new NitriteTableHandler(Dbs.instance).findByService(artifactId);
    }

    public static Single<Boolean> remove(String tableName, String artifactId) {
        return (Mysqls.Instance.get().active()) ?
                new MysqlTableHandler(Mysqls.Instance.get()).remove(tableName, artifactId)  :
                new NitriteTableHandler(Dbs.instance).remove(tableName, artifactId)  ;
    }

    public static Table newInstance() {
        return (Mysqls.Instance.get().active()) ?
                new MysqlTableHandler(Mysqls.Instance.get()).newInstance() :
                new NitriteTableHandler(Dbs.instance).newInstance();
    }

    public static Single<Integer> removeAll() {
        return (Mysqls.Instance.get().active()) ?
                new MysqlTableHandler(Mysqls.Instance.get()).removeAll() :
                new NitriteTableHandler(Dbs.instance).removeAll();
    }

    public String getId() {
        return id;
    }

    public Table setId(String id) {
        this.id = id;
        return this;
    }

    public String getService() {
        return Service;
    }

    public Table setService(String service) {
        Service = service;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public Table setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public Table setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public abstract Single<Boolean> create();
}
