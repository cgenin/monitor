package net.christophe.genin.monitor.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.model.Api;
import net.christophe.genin.monitor.domain.server.model.port.ApiPort;
import rx.Observable;
import rx.Single;

import java.util.Objects;

public class MysqlApi extends Api {
    private final MysqlApiPort handler;
    private final JsonObject document;

    private MysqlApi(MysqlApiPort handler, String method, String path, String idProject, JsonObject document) {
        super(method, path, idProject);
        this.handler = handler;
        this.document = document
                .put(Schemas.Apis.method.name(), method)
                .put(Schemas.Apis.path.name(), path)
                .put(Schemas.Apis.idProject.name(), idProject);
    }

    @Override
    public String id() {
        return document.getString(Schemas.Projects.id.name());
    }

    @Override
    public Api setId(String newId) {
        document.put(Schemas.Projects.id.name(), newId);
        return this;
    }


    @Override
    public Api setArtifactId(String artifactId) {
        document.put(Schemas.Apis.artifactId.name(), artifactId);
        return this;
    }

    @Override
    public Api setGroupId(String groupId) {
        document.put(Schemas.Apis.groupId.name(), groupId);

        return this;
    }

    @Override
    public Api setSince(String version) {
        document.put(Schemas.Apis.since.name(), version);
        return this;
    }

    @Override
    public Api setLatestUpdate(long update) {
        document.put(Schemas.Apis.latestUpdate.name(), update);
        return this;
    }

    @Override
    public Api setName(String name) {
        document.put(Schemas.Apis.name.name(), name);

        return this;
    }

    @Override
    public Api setReturns(String returns) {
        document.put(Schemas.Apis.returns.name(), returns);
        return this;
    }

    @Override
    public Api setParams(String params) {
        document.put(Schemas.Apis.params.name(), params);
        return this;
    }

    @Override
    public Api setComment(String comment) {
        document.put(Schemas.Apis.comment.name(), comment);
        return this;
    }

    @Override
    public Api setClassName(String className) {
        document.put(Schemas.Apis.className.name(), className);
        return this;
    }

    @Override
    public Single<Boolean> create() {
        return handler.create(this);
    }

    @Override
    public String name() {
        return document.getString(Schemas.Apis.name.name());
    }

    @Override
    public String artifactId() {
        return document.getString(Schemas.Apis.artifactId.name());
    }

    @Override
    public String groupId() {
        return document.getString(Schemas.Apis.groupId.name());

    }

    @Override
    public String returns() {
        return document.getString(Schemas.Apis.returns.name());

    }

    @Override
    public String params() {
        return document.getString(Schemas.Apis.params.name());

    }

    @Override
    public String comment() {
        return document.getString(Schemas.Apis.comment.name());

    }

    @Override
    public String since() {
        return document.getString(Schemas.Apis.since.name());

    }

    @Override
    public String className() {
        return document.getString(Schemas.Apis.className.name());
    }

    @Override
    public long latestUpdate() {
        return document.getLong(Schemas.Apis.latestUpdate.name());
    }

    public static class MysqlApiPort implements ApiPort {

        private final Mysqls mysqls;

        public MysqlApiPort(Mysqls mysqls) {
            this.mysqls = mysqls;
        }


        @Override
        public Observable<Integer> deleteByIdProject(String idProject) {
            return mysqls.execute("DELETE from APIS WHERE IDPROJECT=?", new JsonArray().add(idProject))
                    .map(UpdateResult::getUpdated)
                    .toObservable();
        }

        @Override
        public Api newInstance(String method, String path, String idProject) {
            return new MysqlApi(this, method, path, idProject, new JsonObject());
        }

        public Single<Boolean> create(MysqlApi api) {
            return Mysqls.Instance.get().execute(
                    "Insert INTO APIS (ID, IDPROJECT, METHOD, FULLURL, document) VALUES (?,?,?,?, ?)",
                    new JsonArray()
                            .add(api.id())
                            .add(api.idProject)
                            .add(api.method)
                            .add(api.path)
                            .add(api.document.encode()))
                    .map(updateResult -> updateResult.getUpdated() == 1);
        }

        @Override
        public Single<Integer> removeAll() {
            return mysqls.execute("DELETE FROM APIS")
                    .map(UpdateResult::getUpdated);
        }

        @Override
        public Observable<Api> findAll() {
            return mysqls.select("SELECT IDPROJECT, METHOD, FULLURL,  document FROM APIS")
                    .flatMap(rs -> {
                        if (Objects.isNull(rs)) {
                            return Observable.empty();
                        }
                        return Observable.from(rs.getResults());
                    })
                    .map(arr -> {
                        String idProject = arr.getString(0);
                        String method = arr.getString(1);
                        String path = arr.getString(2);
                        String strDocument = arr.getString(3);
                        JsonObject document = new JsonObject(strDocument);

                        return new MysqlApi(this, method, path, idProject, document);
                    });
        }
    }
}
