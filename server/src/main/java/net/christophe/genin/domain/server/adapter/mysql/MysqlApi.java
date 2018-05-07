package net.christophe.genin.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.model.Api;
import rx.Observable;
import rx.Single;

public class MysqlApi extends Api {
    private final MysqlApiHandler handler;
    private final JsonObject document;

    private MysqlApi(MysqlApiHandler handler, String method, String path, String idProject, JsonObject document) {
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

    public static class MysqlApiHandler {

        private final Mysqls mysqls;

        public MysqlApiHandler(Mysqls mysqls) {
            this.mysqls = mysqls;
        }


        public Observable<Integer> deleteByIdProject(String idProject) {
            return mysqls.execute("DELETE from APIS WHERE IDPROJECT=?", new JsonArray().add(idProject))
                    .map(UpdateResult::getUpdated)
                    .toObservable();
        }

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

        public Single<Integer> removeAll() {
            return mysqls.execute("DELETE FROM APIS")
                    .map(UpdateResult::getUpdated);
        }
    }
}
