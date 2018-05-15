package net.christophe.genin.monitor.domain.server.model.port;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.model.StoredServiceEvent;
import rx.Single;

public interface StoredServiceEventPort {

    StoredServiceEvent newInstance(JsonObject json, long lastUpdated);

    Single<Boolean> create(StoredServiceEvent event);

}
