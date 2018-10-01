package net.christophe.genin.monitor.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.command.Treatments;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.StoredServiceEvent;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IStoredServiceEvent;
import rx.Single;

public class MysqlStoredServiceEvent extends StoredServiceEvent {

    private final MysqlIStoredServiceEvent port;


    public static class MysqlIStoredServiceEvent implements IStoredServiceEvent {

        private final Mysqls mysqls;

        public MysqlIStoredServiceEvent(Mysqls mysqls) {
            this.mysqls = mysqls;
        }

        @Override
        public StoredServiceEvent newInstance(JsonObject json, long lastUpdated) {
            return new MysqlStoredServiceEvent(json, lastUpdated, this);
        }

        @Override
        public Single<Boolean> create(StoredServiceEvent event) {
            return mysqls.execute("INSERT INTO STORED_SERVICE_EVENTS (state, document, ARCHIVE, ORIGINAL_ID, LATEST_UPDATE) VALUES (?,?,?,?,?)",
                    new JsonArray().add(Treatments.END.getState()).add(event.json().encode())
                            .add((event.archive()) ? 1 : 0)
                            .add(event.originalId())
                            .add(event.lastUpdated()))
                    .map(updateResult -> updateResult.getUpdated() == 1);
        }
    }

    private MysqlStoredServiceEvent(JsonObject json, long lastUpdated, MysqlIStoredServiceEvent port) {
        super(json, lastUpdated);
        this.port = port;
    }

    @Override
    public Single<Boolean> create() {
        return port.create(this);
    }

}
