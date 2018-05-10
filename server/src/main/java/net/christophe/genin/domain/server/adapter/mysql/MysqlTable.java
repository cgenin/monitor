package net.christophe.genin.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.model.Table;
import net.christophe.genin.domain.server.model.handler.TableHandler;
import rx.Observable;
import rx.Single;

import java.util.*;
import java.util.stream.Collectors;

public class MysqlTable extends Table {

    private final MysqlTableHandler handler;

    private MysqlTable(MysqlTableHandler handler) {
        this.handler = handler;
    }


    public static class MysqlTableHandler implements TableHandler {
        private final Mysqls mysqls;

        public MysqlTableHandler(Mysqls mysqls) {
            this.mysqls = mysqls;
        }

        @Override
        public Observable<Set<String>> findByService(String artifactId) {
            return mysqls.select("SELECT NAME FROM TABLES WHERE SERVICE=?", new JsonArray().add(artifactId))
                    .map(rs -> {
                        if (Objects.isNull(rs)) {
                            return Collections.<String>emptySet();
                        }
                        Set<String> collect = rs
                                .getResults()
                                .stream()
                                .map(row -> row.getString(0))
                                .collect(Collectors.toSet());
                        return collect;
                    });
        }

        @Override
        public Single<Boolean> remove(String tableName, String artifactId) {
            return mysqls.execute(
                    "DELETE FROM TABLES WHERE NAME=? AND SERVICE=?",
                    new JsonArray().add(tableName).add(artifactId))
                    .map(updateResult -> updateResult.getUpdated() == 1);
        }

        @Override
        public Table newInstance() {
            return new MysqlTable(this);
        }


        public Single<Boolean> create(MysqlTable table) {
            return mysqls.execute(
                    "INSERT INTO TABLES (ID, NAME, SERVICE, latestUpdate) VALUES (?,?,?, ?)",
                    new JsonArray()
                            .add(UUID.randomUUID().toString())
                            .add(table.tableName())
                            .add(table.service())
                            .add(table.lastUpdated()))
                    .map(updateResult -> updateResult.getUpdated() == 1);
        }

        @Override
        public Single<Integer> removeAll() {
            return mysqls.execute("DELETE FROM TABLES")
                    .map(UpdateResult::getUpdated);
        }

        @Override
        public Observable<Table> findAll() {
            return mysqls.select("SELECT ID, NAME, SERVICE, latestUpdate from TABLES")
                    .flatMap(rs -> {
                        if (Objects.isNull(rs)) {
                            return Observable.empty();
                        }
                        return Observable.from(rs.getResults());
                    })
                    .map(arr -> {
                        String id = arr.getString(0);
                        String tableName = arr.getString(1);
                        String service = arr.getString(2);
                        Long lastUpdated = arr.getLong(3);
                        return newInstance()
                                .setId(id)
                                .setTableName(tableName)
                                .setService(service)
                                .setLastUpdated(lastUpdated);
                    });
        }

        @Override
        public Observable<HashMap<String, Long>> countTablesByProjects() {
            return mysqls.select(
                            "SELECT SERVICE, count(*) FROM TABLES GROUP BY SERVICE"
                    )
                    .flatMap(rs -> {
                        if (Objects.isNull(rs)) {
                            return Observable.just(new HashMap<String, Long>());
                        }

                        return Observable.from(rs.getResults())
                                .reduce(new HashMap<>(), ((hashMap, row) -> {
                                    hashMap.putIfAbsent(row.getString(0), row.getLong(1));
                                    return hashMap;
                                }));
                    });
        }
    }


    @Override
    public Single<Boolean> create() {
        return handler.create(this);
    }
}
