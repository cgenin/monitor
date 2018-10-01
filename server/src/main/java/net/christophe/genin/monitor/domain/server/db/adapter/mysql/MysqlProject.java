package net.christophe.genin.monitor.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Project;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IProject;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MysqlProject {

    public static class MysqlProjectPort implements ProjectPort {

        private final Mysqls mysqls;

        public MysqlProjectPort(Mysqls mysqls) {
            this.mysqls = mysqls;
        }

        @Override
        public Single<Project> readByName(String artifactId) {
            return mysqls.select("SELECT document from PROJECTS WHERE NAME=?", new JsonArray().add(artifactId))
                    .map(rs -> {
                        if (Objects.isNull(rs) || rs.getResults().isEmpty()) {
                            JsonObject n = new JsonObject().put("name", artifactId)
                                    .put("latestUpdate", 0L);
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
            String id = project.document.getString("id");
            Func1<UpdateResult, Boolean> updateFunc = updateResult -> updateResult.getUpdated() == 1;
            if (Objects.isNull(id)) {
                String idProject = UUID.randomUUID().toString();
                return mysqls.execute("INSERT INTO PROJECTS (ID, NAME, document) " +
                                "  VALUES (?,?,?)",
                        new JsonArray()
                                .add(idProject)
                                .add(project.name())
                                .add(project.document.put("id", idProject).encode())
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

        private final MysqlProjectPort handler;

        private final JsonObject document;

        private ProjectImpl(MysqlProjectPort handler, JsonObject document) {
            this.document = document;
            this.handler = handler;
        }


        @Override
        public long latestUpdate() {
            return document.getLong("latestUpdate");
        }

        @Override
        public Project setName(String artifactId) {
            document.put("name", artifactId);
            return this;
        }

        @Override
        public String name() {
            return document.getString("name", "Not found");
        }

        @Override
        public Project setSnapshot(String version) {
            document.put("snapshot", version);
            return this;
        }

        @Override
        public Project setRelease(String version) {
            document.put("release", version);
            return this;
        }

        @Override
        public Project setTables(List<String> tables) {
            document.put("tables", tables);
            return this;
        }

        @Override
        public List<String> javaDeps() {
            return Jsons.builder(document.getJsonArray("javaDeps")).toListString();
        }

        @Override
        public Project setJavaDeps(List<String> javaDeps) {
            document.put("javaDeps", new JsonArray(javaDeps));
            return this;
        }


        @Override
        public Project setChangeLog(String changeLog) {
            document.put("changelog", changeLog);
            return this;
        }

        @Override
        public Project setApis(List<String> apis) {
            document.put("apis", apis);
            return this;
        }

        @Override
        public Project setLatestUpdate(Long update) {
            document.put("latestUpdate", update);
            return this;
        }

        @Override
        public String id() {
            return document.getString("id");
        }

        @Override
        public String release() {
            return document.getString("release");
        }

        @Override
        public String snapshot() {
            return document.getString("snapshot");
        }

        @Override
        public List<String> tables() {

            return Jsons.builder(document.getJsonArray("tables")).toListString();
        }

        @Override
        public List<String> apis() {
            return Jsons.builder(document.getJsonArray("apis")).toListString();
        }

        @Override
        public String changelog() {
            return document.getString("changelog");
        }

        @Override
        public Single<Boolean> save() {
            return handler.save(this);
        }
    }
}
