package net.christophe.genin.monitor.domain.server.model;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import rx.Observable;
import rx.Single;

public abstract class FrontApps {

    private JsonObject packagesJson;
    private final String groupId;
    private final String artifactId;
    private final String version;
    private long lastUpdate;
    private String id;


    public FrontApps(JsonObject packagesJson, String groupId, String artifactId, String version, long lastUpdate) {
        this.packagesJson = packagesJson;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.lastUpdate = lastUpdate;
    }



    public JsonObject packagesJson() {
        return packagesJson;
    }

    public String groupId() {
        return groupId;
    }

    public String artifactId() {
        return artifactId;
    }

    public String version() {
        return version;
    }

    public long lastUpdate() {
        return lastUpdate;
    }

    public String id() {
        return id;
    }

    public FrontApps setId(String id) {
        this.id = id;
        return this;
    }

    public FrontApps setPackageJson(JsonObject json) {
        this.packagesJson = json;
        return this;
    }

    public FrontApps setLastUpdate(long lastUpdate){
        this.lastUpdate = lastUpdate;
        return this;
    }

    public abstract Single<Boolean> save();
}
