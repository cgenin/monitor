package net.christophe.genin.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.model.Project;
import rx.Single;
import rx.functions.Func1;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MysqlProject {
    public static Single<Project> readByName(String artifactId) {
        Mysqls mysqls = Mysqls.Instance.get();

        return mysqls.select("SELECT document from PROJECTS WHERE NAME=?", new JsonArray().add(artifactId))

                .map(rs -> {
                    if (Objects.isNull(rs) || rs.getResults().isEmpty()) {
                        JsonObject n = new JsonObject().put(Schemas.Projects.name.name(), artifactId)
                                .put(Schemas.Projects.latestUpdate.name(), 0L);
                        return new ProjectImpl(n);
                    }
                    JsonArray firstLine = rs.getResults().get(0);
                    String string = firstLine.getString(0);
                    Project project = new ProjectImpl(new JsonObject(string));
                    return project;
                })
                .first()
                .toSingle();
    }

    private static class ProjectImpl implements Project {

        private final JsonObject document;

        private ProjectImpl(JsonObject document) {
            this.document = document;
        }

        @Override
        public long latestUpdate() {
            return document.getLong(Schemas.Projects.latestUpdate.name());
        }

        @Override
        public Project setName(String artifactId) {
            document.put(Schemas.Projects.name.name(), artifactId);
            return this;
        }

        @Override
        public String name() {
            return document.getString(Schemas.Projects.name.name(), "Not found");
        }

        @Override
        public Project setSnapshot(String version) {
            document.put(Schemas.Projects.snapshot.name(), version);
            return this;
        }

        @Override
        public Project setRelease(String version) {
            document.put(Schemas.Projects.release.name(), version);
            return this;
        }

        @Override
        public Project setTables(List<String> tables) {
            document.put(Schemas.Projects.tables.name(), tables);
            return this;
        }

        @Override
        public Project setJavaDeps(List<String> javaDeps) {
            document.put(Schemas.Projects.javaDeps.name(), javaDeps);
            return this;
        }

        @Override
        public Project setChangeLog(String changeLog) {
            document.put(Schemas.Projects.changelog.name(), changeLog);
            return this;
        }

        @Override
        public Project setApis(List<String> apis) {
            document.put(Schemas.Projects.apis.name(), apis);
            return this;
        }

        @Override
        public Project setLatestUpdate(Long update) {
            document.put(Schemas.Projects.latestUpdate.name(), update);
            return this;
        }

        @Override
        public Single<Boolean> save() {
            Mysqls mysqls = Mysqls.Instance.get();
            String id = document.getString(Schemas.Projects.id.name());
            Func1<UpdateResult, Boolean> updateFunc = updateResult -> updateResult.getUpdated() == 1;
            if (Objects.isNull(id)) {
                String idProject = UUID.randomUUID().toString();
                return mysqls.execute("INSERT INTO PROJECTS (ID, NAME, document) " +
                                "  VALUES (?,?,?)",
                        new JsonArray()
                                .add(idProject)
                                .add(name())
                                .add(document.put(Schemas.Projects.id.name(), idProject).encode())
                )
                        .map(updateFunc);

            }

            return mysqls.execute("UPDATE PROJECTS SET document=? WHERE ID = ?", new JsonArray().add(document.encode()).add(id))
                    .map(updateFunc);
        }
    }
}
