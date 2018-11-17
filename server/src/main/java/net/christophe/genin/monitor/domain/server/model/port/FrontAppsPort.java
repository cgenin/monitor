package net.christophe.genin.monitor.domain.server.model.port;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.model.FrontApps;
import rx.Observable;

/**
 * Port for FrontApi
 */
public interface FrontAppsPort {

    /**
     * Create new instance.
     */
    FrontApps newInstance(JsonObject packagesJson, String groupId, String artifactId, String version);

    Observable<FrontApps> findBy(String groupId, String artifactId, String version);


    Observable<FrontApps> findAll();
}
