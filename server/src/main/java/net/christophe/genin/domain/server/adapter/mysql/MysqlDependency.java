package net.christophe.genin.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.model.Dependency;
import rx.Single;

public class MysqlDependency extends Dependency {

    public static class MysqlDependencyHandler{

        private final Mysqls mysqls;

        public MysqlDependencyHandler(Mysqls mysqls) {
            this.mysqls = mysqls;
        }

        public Single<Integer> removeByUsedBy(String usedBy) {
            return mysqls.execute("DELETE FROM DEPENDENCIES WHERE USED_BY=? ", new JsonArray().add(usedBy))
                    .map(UpdateResult::getUpdated);
        }

        public Single<Boolean> create(String resource, String usedBy) {
            return mysqls.execute("INSERT INTO DEPENDENCIES (RESOURCE, USED_BY) VALUES (?,?)"
                    , new JsonArray().add(resource).add(usedBy))
                    .map(updateResult -> updateResult.getUpdated() == 1);
        }

        public Single<Integer> removeAll() {
            return null;
        }
    }


}
