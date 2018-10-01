package net.christophe.genin.monitor.domain.boundedcontext.domain.model;

import rx.Single;

/**
 * Model which represents an link between an domain and an table.
 */
public abstract class Table {
    private String id;
    private String Service;
    private String tableName;
    private long lastUpdated;

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
