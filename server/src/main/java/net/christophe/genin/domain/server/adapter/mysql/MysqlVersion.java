package net.christophe.genin.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.model.Version;
import rx.Single;
import rx.functions.Func1;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MysqlVersion extends Version {

    private final MysqlVersionHandler handler;

    private MysqlVersion(MysqlVersionHandler handler, String name, String idProject) {
        super(name, idProject);
        this.handler = handler;
    }

    @Override
    public Version setIsSnapshot(boolean snapshot) {
        json().put(Schemas.Version.isSnapshot.name(), snapshot);
        return this;
    }

    @Override
    public Version setJavadeps(List<String> javaDeps) {
        json().put(Schemas.Version.javaDeps.name(), javaDeps);
        return this;
    }

    @Override
    public Version setTables(List<String> tables) {
        json().put(Schemas.Version.tables.name(), tables);
        return this;
    }

    @Override
    public Version setChangeLog(String changeLog) {
        json().put(Schemas.Projects.changelog.name(), changeLog);
        return this;
    }

    @Override
    public Version setApis(List<String> apis) {
        json().put(Schemas.Version.apis.name(), apis);
        return this;
    }

    public String id() {
        return json().getString(Schemas.Version.id.name());
    }

    @Override
    public Single<Boolean> save() {
        return handler.save(this);
    }

    public static class MysqlVersionHandler {
        private final Mysqls mysqls;

        public MysqlVersionHandler(Mysqls mysqls) {
            this.mysqls = mysqls;
        }

        public Single<Boolean> save(MysqlVersion version) {
            Func1<UpdateResult, Boolean> saveToBoolean = updateResult -> updateResult.getUpdated() == 1;
            String id = version.id();
            if (Objects.isNull(id)) {
                String newId = UUID.randomUUID().toString();

                return mysqls.execute(
                        "Insert INTO VERSIONS (ID, IDPROJECT, NAME, document) VALUES (?,?,?,?)",
                        new JsonArray()
                                .add(newId)
                                .add(version.idProject())
                                .add(version.name())
                                .add(version.json()
                                        .put(Schemas.Version.id.name(), newId).encode())
                )
                        .map(saveToBoolean);
            }
            return mysqls.execute("UPDATE VERSIONS SET document=? WHERE ID=?",
                    new JsonArray()
                            .add(version.json().encode())
                            .add(id))
                    .map(saveToBoolean);
        }


        public Single<Version> findByNameAndProjectOrDefault(String version, String idProject) {
            return mysqls.select("SELECT document from VERSIONS WHERE NAME=? AND IDPROJECT = ?", new JsonArray().add(version).add(idProject))
                    .map(rs2 -> {
                        if (Objects.isNull(rs2) || rs2.getResults().isEmpty()) {
                            JsonObject json = new JsonObject()
                                    .put(Schemas.Version.idproject.name(), idProject)
                                    .put(Schemas.Version.name.name(), version)
                                    .put(Schemas.Version.latestUpdate.name(), 0L);

                            return new MysqlVersion(this, version, idProject)
                                    .setJson(json)
                                    .setLatestUpdate(0L);
                        }
                        JsonArray firstRow = rs2.getResults().get(0);
                        String string = firstRow.getString(0);
                        JsonObject json = new JsonObject(string);
                        Long latestUpdate = json.getLong(Schemas.Version.latestUpdate.name());
                        return new MysqlVersion(this, version, idProject)
                                .setJson(json)
                                .setLatestUpdate(latestUpdate);

                    })
                    .first()
                    .toSingle();
        }

        public Single<Integer> removeAll() {
            return mysqls.execute("DELETE FROM VERSIONS")
                    .map(UpdateResult::getUpdated);
        }
    }
}
