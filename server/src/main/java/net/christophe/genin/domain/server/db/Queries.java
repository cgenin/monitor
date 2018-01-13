package net.christophe.genin.domain.server.db;

import io.vertx.core.json.JsonArray;
import net.christophe.genin.domain.server.db.mysql.MysqlQuery;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.db.nitrite.NitriteQuery;
import rx.Single;

public interface Queries {

    static Queries get() {
        return (Mysqls.Instance.get().active()) ? new MysqlQuery() : new NitriteQuery();
    }

    Single<JsonArray> projects();

    Single<JsonArray> tables();

    Single<JsonArray> versions(String idProject);

}
