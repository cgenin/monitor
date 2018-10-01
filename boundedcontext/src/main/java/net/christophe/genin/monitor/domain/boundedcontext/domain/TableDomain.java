package net.christophe.genin.monitor.domain.boundedcontext.domain;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.ITable;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Table;
import net.christophe.genin.monitor.domain.boundedcontext.domain.utils.Schemas;
import rx.Observable;
import rx.Single;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class TableDomain {

    private final ITable tableDao;

    public TableDomain(ITable tableDao) {
        this.tableDao = tableDao;
    }

    public Observable<String> saveOrDelete(List<String> tables, String artifactId, Long update) {
        Observable<String> creationTable = tableDao.findByService(artifactId)
                .flatMap(rs -> {
                    if (Objects.isNull(rs)) {
                        return Observable.from(tables);
                    }
                    HashSet<String> toCreate = new HashSet<>(tables);
                    toCreate.removeAll(rs);
                    return Observable.from(toCreate);
                })
                .flatMap(tableName -> {
                    Table newTable = tableDao.newInstance();
                    newTable.setTableName(tableName)
                            .setService(artifactId)
                            .setLastUpdated(update);
                    return newTable.create()
                            .map(updateResult -> "Table '" + tableName + "' for '" + artifactId + "' creation " + updateResult)
                            .toObservable();
                });
        Observable<String> deletion = tableDao.findByService(artifactId)
                .flatMap(rs -> {
                    if (Objects.isNull(rs)) {
                        return Observable.empty();
                    }

                    rs.removeAll(tables);
                    return Observable.from(rs);
                })
                .flatMap(tableName -> tableDao.remove(tableName, artifactId)
                        .map(updateResult -> "Table '" + tableName + "' for '" + artifactId + "'  deleted :" + updateResult)
                        .toObservable()
                );
        return Observable.concat(deletion, creationTable);
    }

    public Observable<JsonArray> extract(){
        return tableDao.findAll()
                .map(table -> new JsonObject()
                        .put("ID", table.id())
                        .put("NAME", table.tableName())
                        .put("SERVICE", table.service())
                        .put("latestUpdate", table.lastUpdated()))
                .reduce(new JsonArray(), JsonArray::add);
    }

    public Observable<JsonObject> findAll() {
        return tableDao.findAll()
                .reduce(new HashMap<String, JsonObject>(),
                        (m, row) -> {
                            String name = row.tableName();
                            String service = row.service();
                            Long update = row.lastUpdated();
                            JsonObject obj = m.getOrDefault(name, new JsonObject()
                                    .put(Schemas.Tables.id.name(), name)
                                    .put(Schemas.Tables.name.name(), name)
                                    .put(Schemas.Tables.latestUpdate.name(), update)
                            );
                            JsonArray services = obj.getJsonArray(Schemas.Tables.services.name(), new JsonArray())
                                    .add(service);
                            obj.put(Schemas.Tables.services.name(), services);
                            m.put(name, obj);
                            return m;
                        })
                .flatMap(hasmap -> Observable.from(hasmap.values()));
    }

    public Observable<JsonObject> statsByProject() {
        return tableDao.countTablesByProjects()
                .flatMap(hasmap -> Observable.from(hasmap.entrySet()))
                .reduce(new JsonObject(), (acc, e) -> acc.put(e.getKey(), e.getValue()));
    }



    public Single<Integer> removeAll() {
        return tableDao.removeAll();
    }


}
