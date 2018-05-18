package net.christophe.genin.monitor.domain.server.db.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.asyncsql.AsyncSQLClient;
import io.vertx.rxjava.ext.asyncsql.MySQLClient;
import io.vertx.rxjava.ext.sql.SQLConnection;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Instance for managing mysql's connection and current query.
 */
public interface Mysqls {
    /**
     * Test an connection.
     *
     * @param vertx         the vertx instance.
     * @param configuration the configuration object.
     * @return true if OK.
     */
    static Single<Boolean> test(Vertx vertx, JsonObject configuration) {
        return Single.fromCallable(() -> MySQLClient.createNonShared(vertx, configuration))
                .subscribeOn(Schedulers.io())
                .flatMap(client -> client.rxQuerySingle("SELECT 1 from DUAL"))
                .map(rs -> {
                    if (rs.isEmpty()) {
                        return false;
                    }
                    return rs.getInteger(0) == 1;
                });
    }

    default boolean active() {
        return false;
    }

    default Single<SQLConnection> connection() {
        return Observable.<SQLConnection>empty().toSingle();
    }

    default Observable<ResultSet> select(String sql) {
        return Observable.empty();
    }

    default Observable<ResultSet> select(String sql, JsonArray params) {
        return Observable.empty();
    }

    default Single<UpdateResult> execute(String sql) {
        return Observable.<UpdateResult>empty().toSingle();
    }

    default Single<UpdateResult> execute(String sql, JsonArray params) {
        return Observable.<UpdateResult>empty().toSingle();
    }

    default Single<List<Integer>> batch(String... batchOperations) {
        return Observable.<List<Integer>>empty().toSingle();
    }

    /**
     * Singleton which store the current instance.
     */
    final class Instance {
        private static Mysqls instance = new NullMysql();

        /**
         * Update the singleton
         *
         * @param vertx         The vertx instance.
         * @param configuration the current configuration.
         * @return the updated instance
         */
        public synchronized static Mysqls set(Vertx vertx, JsonObject configuration) {

            instance = Optional.ofNullable(configuration)
                    .map(config -> new VertxMysql(vertx, config).initialize())
                    .orElse(new NullMysql());
            return instance;
        }

        public synchronized static Mysqls get() {
            return instance;
        }

        public synchronized static boolean disabled() {
            instance = new NullMysql();
            return instance.active();
        }
    }

    /**
     * An not open connection.
     */
    class NullMysql implements Mysqls {
    }

    /**
     * An implementation for the
     */
    class VertxMysql implements Mysqls {
        private static final Logger logger = LoggerFactory.getLogger(VertxMysql.class);


        private final Vertx vertx;
        private final JsonObject config;

        VertxMysql(Vertx vertx, JsonObject config) {
            this.vertx = vertx;
            this.config = config;
        }

        private Mysqls initialize() {
            AsyncSQLClient shared = getShared();
            logger.info("db mysql initialized : " + shared);
            return this;
        }

        private AsyncSQLClient getShared() {
            return MySQLClient.createShared(vertx, config, "antimonitor.pool");
        }

        @Override
        public boolean active() {
            return true;
        }

        @Override
        public Single<SQLConnection> connection() {
            return getShared().rxGetConnection().observeOn(Schedulers.io());
        }

        @Override
        public Observable<ResultSet> select(String sql) {
            return select(sql, new JsonArray());
        }

        @Override
        public Observable<ResultSet> select(String sql, JsonArray params) {
            if (params.isEmpty()) {
                return connection().flatMap(conn -> conn.rxSetAutoCommit(true)
                        .flatMap(v -> conn.rxQuery(sql))
                        .flatMap(rs -> conn.rxClose().map(v -> rs))
                ).toObservable();
            }

            return connection().flatMap(conn -> conn.rxSetAutoCommit(true)
                    .flatMap(v -> conn.rxQueryWithParams(sql, params))
                    .flatMap(rs -> conn.rxClose().map(v -> rs))
            ).toObservable();
        }


        @Override
        public Single<List<Integer>> batch(String... batchOperations) {
            List<Integer> initialValue = new ArrayList<>();
            List<Observable<UpdateResult>> collect = Arrays.stream(batchOperations)
                    .map(sql -> {
                        logger.info("execute : " + sql);
                        return execute(sql)
                                .toObservable();
                    })
                    .collect(Collectors.toList());
            return Observable.concat(collect)
                    .map(UpdateResult::getUpdated)
                    .reduce(initialValue, (acc, nb) -> {
                        acc.add(nb);
                        return acc;
                    })
                    .toSingle();
        }

        @Override
        public Single<UpdateResult> execute(String sql) {
            return getShared().rxUpdateWithParams(sql, new JsonArray());
        }

        @Override
        public Single<UpdateResult> execute(String sql, JsonArray params) {
            if (params.isEmpty()) {
                return getShared()
                        .rxUpdate(sql)
                        .observeOn(Schedulers.io());
            }
            return getShared()
                    .rxUpdateWithParams(sql, params)
                    .observeOn(Schedulers.io());
        }
    }
}
