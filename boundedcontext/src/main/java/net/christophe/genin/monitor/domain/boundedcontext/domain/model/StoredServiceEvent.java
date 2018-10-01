package net.christophe.genin.monitor.domain.server.model;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import rx.Single;

public abstract class StoredServiceEvent {

    private final JsonObject json;
    private final long lastUpdated;
    private long id;
    private boolean archive = true;
    private long originalId;



    public StoredServiceEvent(JsonObject json, long lastUpdated) {
        this.json = json;
        this.lastUpdated = lastUpdated;
    }

    public JsonObject json() {
        return json;
    }

    public long lastUpdated() {
        return lastUpdated;
    }

    public long id() {
        return id;
    }

    public StoredServiceEvent setId(long id) {
        this.id = id;
        return this;
    }

    public boolean archive() {
        return archive;
    }

    public StoredServiceEvent setArchive(boolean archive) {
        this.archive = archive;
        return this;
    }

    public abstract Single<Boolean> create();

    public StoredServiceEvent setOriginalId(long id) {
        this.originalId = id;
        return this;
    }

    public long originalId() {
        return originalId;
    }
}
