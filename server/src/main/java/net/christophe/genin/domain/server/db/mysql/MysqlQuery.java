package net.christophe.genin.domain.server.db.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.db.Queries;
import net.christophe.genin.domain.server.json.Jsons;
import rx.Observable;
import rx.Single;

import java.util.Objects;

public class MysqlQuery implements Queries {
    @Override
    public Single<JsonArray> projects() {
        return Mysqls.Instance.get().select("SELECT document from PROJECTS")
                .map(rs -> {
                    if (Objects.isNull(rs)) {
                        return new JsonArray();
                    }
                    return rs.getResults().stream()
                            .map(line -> {
                                String string = line.getString(0);
                                return new JsonObject(string);
                            })
                            .collect(Jsons.Collectors.toJsonArray());
                })
                .switchIfEmpty(Observable.just(new JsonArray()))
                .toSingle();

    }
}
