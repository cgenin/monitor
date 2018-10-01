package net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.StoredServiceEvent;
import rx.Single;

public interface IStoredServiceEvent {

    StoredServiceEvent newInstance(JsonObject json, long lastUpdated);

    Single<Boolean> create(StoredServiceEvent event);

}
