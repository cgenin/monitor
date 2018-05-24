package net.christophe.genin.monitor.domain.server.adapter.mysql;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.adapter.nitrite.NitriteRaw;
import net.christophe.genin.monitor.domain.server.command.Treatments;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.model.Raw;
import net.christophe.genin.monitor.domain.server.model.port.RawPort;
import rx.Observable;
import rx.Single;

public class MysqlRaw implements Raw {

    private final MysqlRawPort handler;
    private final JsonObject json;
    private final Long id;
    private final Integer state;

    public MysqlRaw(MysqlRawPort handler, Long id, JsonObject json, Integer state) {
        this.handler = handler;
        this.json = json;
        this.id = id;
        this.state = state;
    }

    @Override
    public JsonObject json() {
        return json;
    }

    @Override
    public Treatments state() {
        return Treatments.parse(state);
    }

    @Override
    public String artifactId() {
        return json.getString("artifactId");
    }

    @Override
    public Long update() {
        return  json.getLong("update");
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public Single<Boolean> updateState(Treatments treatments) {
        return handler.updateState(this, treatments);
    }

    public static class MysqlRawPort implements RawPort {

        private final Mysqls mysqls;

        public MysqlRawPort(Mysqls mysqls) {
            this.mysqls = mysqls;
        }

        @Override
        public Single<Long> save(JsonObject object) {
            return null;
        }

        public Single<Boolean> updateState(MysqlRaw raw, Treatments treatments) {
            return null;
        }

        @Override
        public Single<Integer> updateAllStatesBy(Treatments treatments) {
            return null;
        }

        @Override
        public Observable<Raw> findByStateFirst(Treatments treatments) {
            return null;
        }

        @Override
        public Observable<Raw> findAllByState(Treatments treatments) {
            return null;
        }

        @Override
        public Observable<Raw> findAll() {
            return null;
        }

        @Override
        public Single<Boolean> removeById(long id) {
            return null;
        }
    }
}
