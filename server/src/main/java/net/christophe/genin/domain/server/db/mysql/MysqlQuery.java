package net.christophe.genin.domain.server.db.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.domain.server.db.Queries;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MysqlQuery implements Queries {


    @Override
    public Single<JsonArray> projects() {
        return execute("SELECT document from PROJECTS");

    }

    public Single<JsonArray> execute(String sql) {
        return Mysqls.Instance.get().select(sql)
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
    public Single<JsonArray> apis() {
        return execute("SELECT document FROM APIS");
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

    @Override
    public Single<JsonArray> listAllResourceDependencies() {
        return Mysqls.Instance.get().select("SELECT RESOURCE FROM DEPENDENCIES GROUP BY RESOURCE ORDER BY 1")
                .map(rs -> {
                    if (Objects.isNull(rs)) {
                        return new JsonArray();
                    }
                    return rs.getResults()
                            .stream()
                            .map(row -> row.getString(0))
                            .collect(Jsons.Collectors.toJsonArray());
                })
                .switchIfEmpty(Observable.just(new JsonArray()))
                .toSingle();
    }

    @Override
    public Single<JsonArray> usedBy(String resource) {
        return Mysqls.Instance.get()
                .select(
                        "SELECT USED_BY FROM DEPENDENCIES WHERE RESOURCE=? order by 1",
                        new JsonArray().add(resource)
                )
                .map(rs -> {
                    if (Objects.isNull(rs)) {
                        return new JsonArray();
                    }
                    return rs.getResults()
                            .stream()
                            .map(row -> row.getString(0))
                            .collect(Jsons.Collectors.toJsonArray());
                })
                .switchIfEmpty(Observable.just(new JsonArray()))
                .toSingle();
    }

    @Override
    public Single<JsonObject> tablesByProjects() {
        return Mysqls.Instance.get()
                .select(
                        "SELECT SERVICE, count(*) FROM TABLES GROUP BY SERVICE"
                )
                .map(rs -> {
                    if (Objects.isNull(rs)) {
                        return new JsonObject();
                    }
                    return rs.getResults()
                            .stream()
                            .map(row -> new JsonObject().put(row.getString(0), row.getLong(1)))
                            .reduce(new JsonObject(), (acc, obj) -> obj.mergeIn(acc));
                })
                .switchIfEmpty(Observable.just(new JsonObject()))
                .toSingle();
    }

    @Override
    public Single<JsonObject> dump() {
        Observable<JsonObject> apis = Mysqls.Instance.get()
                .select("SELECT ID, METHOD, FULLURL, IDPROJECT, document from APIS")
                .map(rs -> {
                    if (Objects.isNull(rs)) {
                        return new JsonArray();
                    }
                    return rs.getResults()
                            .stream()
                            .map(row -> new JsonObject()
                                    .put("ID", row.getString(0))
                                    .put("METHOD", row.getString(1))
                                    .put("FULLURL", row.getString(2))
                                    .put("IDPROJECT", row.getString(3))
                                    .put("document", row.getString(4)))
                            .collect(Jsons.Collectors.toJsonArray());
                })
                .map(arr -> new JsonObject().put("apis", arr));

        Observable<JsonObject> projects = Mysqls.Instance.get()
                .select("SELECT ID, NAME, document from PROJECTS")
                .map(rs -> {
                    if (Objects.isNull(rs)) {
                        return new JsonArray();
                    }
                    return rs.getResults()
                            .stream()
                            .map(row -> new JsonObject()
                                    .put("ID", row.getString(0))
                                    .put("NAME", row.getString(1))
                                    .put("document", row.getString(2)))
                            .collect(Jsons.Collectors.toJsonArray());
                })
                .map(arr -> new JsonObject().put("projects", arr));

        Observable<JsonObject> tables = Mysqls.Instance.get()
                .select("SELECT ID, NAME,SERVICE, latestUpdate from TABLES")
                .map(rs -> {
                    if (Objects.isNull(rs)) {
                        return new JsonArray();
                    }
                    return rs.getResults()
                            .stream()
                            .map(row -> new JsonObject()
                                    .put("ID", row.getString(0))
                                    .put("NAME", row.getString(1))
                                    .put("SERVICE", row.getString(2))
                                    .put("latestUpdate", row.getLong(3))
                            )
                            .collect(Jsons.Collectors.toJsonArray());
                })
                .map(arr -> new JsonObject().put("tables", arr));


        Observable<JsonObject> versions = Mysqls.Instance.get()
                .select("SELECT ID, NAME, IDPROJECT, document from VERSIONS")
                .map(rs -> {
                    if (Objects.isNull(rs)) {
                        return new JsonArray();
                    }
                    return rs.getResults()
                            .stream()
                            .map(row -> new JsonObject()
                                    .put("ID", row.getString(0))
                                    .put("NAME", row.getString(1))
                                    .put("IDPROJECT", row.getString(2))
                                    .put("document", row.getString(3)))
                            .collect(Jsons.Collectors.toJsonArray());
                })
                .map(arr -> new JsonObject().put("versions", arr));

        Observable<JsonObject> dependencies = Mysqls.Instance.get()
                .select("SELECT RESOURCE, USED_BY, document from DEPENDENCIES")
                .map(rs -> {
                    if (Objects.isNull(rs)) {
                        return new JsonArray();
                    }
                    return rs.getResults()
                            .stream()
                            .map(row -> new JsonObject()
                                    .put("RESOURCE", row.getString(0))
                                    .put("USED_BY", row.getString(1))
                                    .put("document", row.getString(2)))
                            .collect(Jsons.Collectors.toJsonArray());
                })
                .map(arr -> new JsonObject().put("dependencies", arr));
        Observable<JsonObject> raws = Observable.fromCallable(
                () -> Dbs.instance
                        .getCollection(Schemas.RAW_COLLECTION)
                        .find()
                        .toList()
                        .stream()
                        .map(doc -> Dbs.Raws.toJson(doc).put("state", doc.get("state")))
                        .collect(Jsons.Collectors.toJsonArray()))
                .map(arr -> new JsonObject().put("raws", arr))
                .observeOn(Schedulers.io());

        return Observable.concat(apis, projects, dependencies, versions, tables, raws)
                .reduce(new JsonObject(), JsonObject::mergeIn)
                .toSingle();
    }
}
