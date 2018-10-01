package net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.FrontApps;
import rx.Observable;

/**
 * Port for FrontApi
 */
public interface IFrontApps {

    /**
     * Create new instance.
     */
    FrontApps newInstance(JsonObject packagesJson, String groupId, String artifactId, String version);

    Observable<FrontApps> findBy(String groupId, String artifactId, String version);


    Observable<FrontApps> findAll();
}
