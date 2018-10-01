package net.christophe.genin.monitor.domain.boundedcontext.domain;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IStoredServiceEvent;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.StoredServiceEvent;

public class StoredServiceEventDomain {

    private final IStoredServiceEvent storedDao;

    public StoredServiceEventDomain(IStoredServiceEvent storedDao) {
        this.storedDao = storedDao;
    }

    public  StoredServiceEvent newInstance(JsonObject json, long lastUpdated) {
        return storedDao.newInstance(json, lastUpdated);
    }
}
