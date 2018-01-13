package net.christophe.genin.domain.server.db.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.db.Queries;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.json.Jsons;
import rx.Observable;
import rx.Single;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @SuppressWarnings("unchecked")
    @Override
    public Single<JsonArray> tables() {
        return Mysqls.Instance.get().select("select NAME, SERVICE, latestUpdate FROM TABLES order by 1, 2")
                .map(rs -> {
                    if (Objects.isNull(rs)) {
                        return new JsonArray();
                    }
                    Map<String, JsonObject> ma = rs.getResults()
                            .stream()
                            .collect(HashMap::new,
                                    (m, row) -> {
                                        String name = row.getString(0);
                                        String service = row.getString(1);
                                        Long update = row.getLong(2);
                                        JsonObject obj = m.getOrDefault(name, new JsonObject()
                                                .put(Schemas.Tables.id.name(), name)
                                                .put(Schemas.Tables.name.name(), name)
                                                .put(Schemas.Tables.latestUpdate.name(), update)
                                        );
                                        JsonArray services = obj.getJsonArray(Schemas.Tables.services.name(), new JsonArray())
                                                .add(service);
                                        obj.put(Schemas.Tables.services.name(), services);
                                        m.put(name, obj);
                                    },
                                    (m, u) -> {
                                    });
                    return new JsonArray(new ArrayList(ma.values()));

                })
                .switchIfEmpty(Observable.just(new JsonArray()))
                .toSingle();

    }

    @Override
    public Single<JsonArray> versions(String idProject) {
        return Mysqls.Instance.get().select("select document FROM VERSIONS WHERE IDPROJECT=?", new JsonArray().add(idProject))
                .map(rs -> {
                    if (Objects.isNull(rs)) {
                        return new JsonArray();
                    }
                    return rs.getResults()
                            .stream()
                            .map(row -> {
                                String doc = row.getString(0);
                                return new JsonObject(doc);
                            }).collect(Jsons.Collectors.toJsonArray());
                })
                .switchIfEmpty(Observable.just(new JsonArray()))
                .toSingle();
    }
}
