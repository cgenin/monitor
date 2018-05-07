package net.christophe.genin.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.model.Table;
import rx.Observable;
import rx.Single;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class MysqlTable extends Table {

    private final MysqlTableHandler handler;

    private MysqlTable(MysqlTableHandler handler) {
        this.handler = handler;
    }


    public static class MysqlTableHandler {
        private final Mysqls mysqls;

        public MysqlTableHandler(Mysqls mysqls) {
            this.mysqls = mysqls;
        }

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

        public Single<Boolean> remove(String tableName, String artifactId) {
            return mysqls.execute(
                    "DELETE FROM TABLES WHERE NAME=? AND SERVICE=?",
                    new JsonArray().add(tableName).add(artifactId))
                    .map(updateResult -> updateResult.getUpdated() == 1);
        }

        public Table newInstance() {
            return new MysqlTable(this);
        }


        public Single<Boolean> create(MysqlTable table) {
            return mysqls.execute(
                    "INSERT INTO TABLES (ID, NAME, SERVICE, latestUpdate) VALUES (?,?,?, ?)",
                    new JsonArray()
                            .add(UUID.randomUUID().toString())
                            .add(table.getTableName())
                            .add(table.getService())
                            .add(table.getLastUpdated()))
                    .map(updateResult -> updateResult.getUpdated() == 1);
        }

        public Single<Integer> removeAll() {
            return mysqls.execute("DELETE FROM TABLES")
                    .map(UpdateResult::getUpdated);
        }
    }


    @Override
    public Single<Boolean> create() {
        return handler.create(this);
    }
}
