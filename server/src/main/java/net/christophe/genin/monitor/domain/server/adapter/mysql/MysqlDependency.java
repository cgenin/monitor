package net.christophe.genin.monitor.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.model.Dependency;
import net.christophe.genin.monitor.domain.server.model.port.DependencyPort;
import rx.Observable;
import rx.Single;

import java.util.Objects;

public class MysqlDependency extends Dependency {

    public MysqlDependency(String resource, String usedBy) {
        super(resource, usedBy);
    }

    public static class MysqlDependencyPort implements DependencyPort {

        private final Mysqls mysqls;

        public MysqlDependencyPort(Mysqls mysqls) {
            this.mysqls = mysqls;
        }

        @Override
        public Single<Integer> removeByUsedBy(String usedBy) {
            return mysqls.execute("DELETE FROM DEPENDENCIES WHERE USED_BY=? ", new JsonArray().add(usedBy))
                    .map(UpdateResult::getUpdated);
        }

        @Override
        public Single<Boolean> create(String resource, String usedBy) {
            return mysqls.execute("INSERT INTO DEPENDENCIES (RESOURCE, USED_BY) VALUES (?,?)"
                    , new JsonArray().add(resource).add(usedBy))
                    .map(updateResult -> updateResult.getUpdated() == 1);
        }

        @Override
        public Single<Integer> removeAll() {
            return mysqls.execute("DELETE FROM DEPENDENCIES")
                    .map(UpdateResult::getUpdated);
        }

        @Override
        public Observable<String> findAllResource() {
            return Mysqls.Instance.get().select("SELECT RESOURCE FROM DEPENDENCIES GROUP BY RESOURCE ORDER BY 1")
                    .flatMap(rs -> {
                        if (Objects.isNull(rs)) {
                            return Observable.empty();
                        }
                        return Observable.from(rs.getResults());
                    })
                    .map(row -> row.getString(0));

        }

        @Override
        public Observable<String> usedBy(String resource) {
            return mysqls.select(
                    "SELECT USED_BY FROM DEPENDENCIES WHERE RESOURCE=? order by 1",
                    new JsonArray().add(resource)
            )
                    .flatMap(rs -> {
                        if (Objects.isNull(rs)) {
                            return Observable.empty();
                        }
                        return Observable.from(rs.getResults());
                    })
                    .map(row -> row.getString(0));

        }

        @Override
        public Observable<Dependency> findAll() {
            return mysqls.select("SELECT RESOURCE, USED_BY FROM DEPENDENCIES")
                    .flatMap(rs -> {
                        if (Objects.isNull(rs)) {
                            return Observable.empty();
                        }
                        return Observable.from(rs.getResults());
                    })
                    .map(row -> new MysqlDependency(row.getString(0), row.getString(1)));
        }
    }


}
