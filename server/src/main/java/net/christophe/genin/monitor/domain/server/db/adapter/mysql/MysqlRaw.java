package net.christophe.genin.monitor.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.monitor.domain.boundedcontext.domain.Treatments;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Raw;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IRaw;
import rx.Observable;
import rx.Single;

public class MysqlRaw implements Raw {

    private static final Logger logger = LoggerFactory.getLogger(MysqlRaw.class);


    private final MysqlIRaw handler;
    private final JsonObject json;
    private final Long id;
    private final Integer state;

    private MysqlRaw(MysqlIRaw handler, Long id, JsonObject json, Integer state) {
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
        return json.getLong("update");
    }


    @Override
    public Long id() {
        return id;
    }


    @Override
    public Single<Boolean> updateState(Treatments treatments) {
        return handler.updateState(this, treatments);
    }

    public static class MysqlIRaw implements IRaw {

        private final Mysqls mysqls;

        public MysqlIRaw(Mysqls mysqls) {
            this.mysqls = mysqls;
        }

        @Override
        public Single<Long> save(JsonObject object) {
            Long update = object.getLong("update");
            JsonArray params = new JsonArray()
                    .add(Treatments.PROJECTS.getState())
                    .add(object.encode())
                    .add(update);
            return mysqls.execute("INSERT INTO QUEUE (state, document, LATEST_UPDATE) VALUES (?,?,?)", params)
                    .map(updateResult -> {
                        logger.info("Insert Ok : " + (updateResult.getUpdated() == 1));
                        Long id = updateResult.getKeys().getLong(0);
                        logger.info("Insert with id : " + id);
                        return id;
                    });
        }

        public Single<Boolean> updateState(MysqlRaw raw, Treatments treatments) {
            Integer state = treatments.getState();
            Long id = raw.id();
            return mysqls.execute("UPDATE QUEUE set state=? where ID=?", new JsonArray().add(state).add(id))
                    .map(updateResult -> (updateResult.getUpdated() == 1));
        }

        @Override
        public Single<Integer> updateAllStatesBy(Treatments treatments) {
            Integer state = treatments.getState();
            return mysqls.execute("UPDATE QUEUE set state=?", new JsonArray().add(state))
                    .map(UpdateResult::getUpdated);
        }

        @Override
        public Observable<Raw> findByStateFirst(Treatments treatments) {
            JsonArray params = new JsonArray().add(treatments.getState());
            return mysqls.select("SELECT ID, state, document, LATEST_UPDATE FROM QUEUE WHERE  state=? ORDER BY LATEST_UPDATE LIMIT 1", params)
                    .flatMap(resultSet -> {
                        if (resultSet.getResults().isEmpty()) {
                            return Observable.empty();
                        }
                        JsonArray res = resultSet.getResults().get(0);

                        MysqlRaw value = toMysqlRaw(res);
                        return Observable.just(value);
                    });
        }

        private MysqlRaw toMysqlRaw(JsonArray res) {
            Long id = res.getLong(0);
            Integer state = res.getInteger(1);
            String doc = res.getString(2);
            JsonObject json = new JsonObject(doc);

            return new MysqlRaw(this, id, json, state);
        }

        @Override
        public Observable<Raw> findAllByState(Treatments treatments) {
            JsonArray params = new JsonArray().add(treatments.getState());
            return mysqls.select("SELECT ID, state, document,LATEST_UPDATE FROM QUEUE WHERE state=?  ORDER BY ID", params)
                    .flatMap(resultSet -> Observable.from(resultSet.getResults()))
                    .map(this::toMysqlRaw);
        }

        @Override
        public Observable<Raw> findAll() {
            return mysqls.select("SELECT ID, state, document, LATEST_UPDATE FROM QUEUE ORDER BY ID")
                    .flatMap(resultSet -> Observable.from(resultSet.getResults()))
                    .map(this::toMysqlRaw);
        }

        @Override
        public Single<Boolean> removeById(long id) {
            return mysqls.execute("DELETE FROM QUEUE WHERE ID=?", new JsonArray().add(id))
                    .map(updateResult -> (updateResult.getUpdated() == 1));
        }
    }


}
