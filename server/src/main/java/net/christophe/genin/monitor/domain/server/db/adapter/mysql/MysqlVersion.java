package net.christophe.genin.monitor.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Version;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IVersion;
import rx.Observable;
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

    @Override
    public boolean isSnapshot() {
        return json().getBoolean(Schemas.Version.isSnapshot.name());

    }

    @Override
    public String changelog() {
        return json().getString(Schemas.Version.changelog.name());
    }

    @Override
    public List<String> tables() {
        return Jsons.builder(json().getJsonArray(Schemas.Version.tables.name())).toListString();
    }

    @Override
    public List<String> apis() {
        return Jsons.builder(json().getJsonArray(Schemas.Version.apis.name())).toListString();
    }

    @Override
    public List<String> javaDeps() {
        return Jsons.builder(json().getJsonArray(Schemas.Version.javaDeps.name())).toListString();

    }

    public static class MysqlVersionHandler implements VersionPort {
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


        @Override
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

        @Override
        public Single<Integer> removeAll() {
            return mysqls.execute("DELETE FROM VERSIONS")
                    .map(UpdateResult::getUpdated);
        }


        @Override
        public Observable<Version> findByProject(String idProject) {
            return mysqls.select("SELECT document from VERSIONS WHERE IDPROJECT = ?", new JsonArray().add(idProject))
                    .flatMap(rs2 -> {
                        if (Objects.isNull(rs2) || rs2.getResults().isEmpty()) {
                            return Observable.empty();
                        }
                        return Observable.from(rs2.getResults());

                    })
                    .map(arr -> arr.getString(0))
                    .map(JsonObject::new)
                    .map(this::toVersion);
        }

        @Override
        public Observable<Version> findAll() {
            return mysqls.select("SELECT  IDPROJECT, NAME, document from VERSIONS")
                    .flatMap(rs -> {
                        if (Objects.isNull(rs)) {
                            return Observable.empty();
                        }
                        return Observable.from(rs.getResults());
                    })
                    .map(arr -> {
                        String idProject = arr.getString(0);
                        String version = arr.getString(1);
                        String strDoc = arr.getString(2);
                        JsonObject json = new JsonObject(strDoc);
                        return new MysqlVersion(this, version, idProject)
                                .setJson(json);
                    });
        }

        private Version toVersion(JsonObject json) {
            String idProject = json.getString(Schemas.Version.idproject.name());
            String name = json.getString(Schemas.Version.name.name());
            Long latestUpdate = json.getLong(Schemas.Version.latestUpdate.name(), 0L);
            return new MysqlVersion(this, name, idProject)
                    .setJson(json)
                    .setLatestUpdate(latestUpdate);
        }
    }
}
