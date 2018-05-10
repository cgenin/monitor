package net.christophe.genin.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.model.Project;
import net.christophe.genin.domain.server.model.handler.ProjectHandler;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MysqlProject {

    public static class MysqlProjectHandler implements ProjectHandler {

        private final Mysqls mysqls;

        public MysqlProjectHandler(Mysqls mysqls) {
            this.mysqls = mysqls;
        }

        @Override
        public Single<Project> readByName(String artifactId) {
            return mysqls.select("SELECT document from PROJECTS WHERE NAME=?", new JsonArray().add(artifactId))
                    .map(rs -> {
                        if (Objects.isNull(rs) || rs.getResults().isEmpty()) {
                            JsonObject n = new JsonObject().put(Schemas.Projects.name.name(), artifactId)
                                    .put(Schemas.Projects.latestUpdate.name(), 0L);
                            return toProject(n);
                        }
                        JsonArray firstLine = rs.getResults().get(0);
                        String string = firstLine.getString(0);
                        JsonObject document = new JsonObject(string);
                        return toProject(document);
                    })
                    .first()
                    .toSingle();
        }

        private Project toProject(JsonObject document) {
            return new ProjectImpl(this, document);
        }

        @Override
        public Single<Integer> removeAll() {
            return mysqls.execute("DELETE FROM  PROJECTS")
                    .map(UpdateResult::getUpdated);
        }

        public Single<Boolean> save(ProjectImpl project) {
            String id = project.document.getString(Schemas.Projects.id.name());
            Func1<UpdateResult, Boolean> updateFunc = updateResult -> updateResult.getUpdated() == 1;
            if (Objects.isNull(id)) {
                String idProject = UUID.randomUUID().toString();
                return mysqls.execute("INSERT INTO PROJECTS (ID, NAME, document) " +
                                "  VALUES (?,?,?)",
                        new JsonArray()
                                .add(idProject)
                                .add(project.name())
                                .add(project.document.put(Schemas.Projects.id.name(), idProject).encode())
                )
                        .map(updateFunc);

            }

            return mysqls.execute("UPDATE PROJECTS SET document=? WHERE ID = ?", new JsonArray().add(project.document.encode()).add(id))
                    .map(updateFunc);
        }

        @Override
        public Observable<Project> findAll() {
            return mysqls.select("SELECT document from PROJECTS ")
                    .flatMap(rs -> {
                        if (Objects.isNull(rs) || rs.getResults().isEmpty()) {
                            return Observable.empty();
                        }
                        return Observable.from(rs.getResults());
                    })
                    .map(arr -> arr.getString(0))
                    .map(JsonObject::new)
                    .map(this::toProject);
        }
    }


    private static class ProjectImpl implements Project {

        private final MysqlProjectHandler handler;

        private final JsonObject document;

        private ProjectImpl(MysqlProjectHandler handler, JsonObject document) {
            this.document = document;
            this.handler = handler;
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
        public List<String> javaDeps() {
            return Jsons.builder(document.getJsonArray(Schemas.Projects.javaDeps.name())).toListString();
        }

        @Override
        public Project setJavaDeps(List<String> javaDeps) {
            document.put(Schemas.Projects.javaDeps.name(), new JsonArray(javaDeps));
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
        public String id() {
            return document.getString(Schemas.Projects.id.name());
        }

        @Override
        public String release() {
            return document.getString(Schemas.Projects.release.name());
        }

        @Override
        public String snapshot() {
            return document.getString(Schemas.Projects.snapshot.name());
        }

        @Override
        public List<String> tables() {

            return Jsons.builder(document.getJsonArray(Schemas.Projects.tables.name())).toListString();
        }

        @Override
        public List<String> apis() {
            return Jsons.builder(document.getJsonArray(Schemas.Projects.apis.name())).toListString();
        }

        @Override
        public String changelog() {
            return document.getString(Schemas.Projects.changelog.name());
        }

        @Override
        public Single<Boolean> save() {
            return handler.save(this);
        }
    }
}
