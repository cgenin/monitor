package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitritePort;
import net.christophe.genin.monitor.domain.server.model.Api;
import net.christophe.genin.monitor.domain.server.model.port.ApiPort;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.WriteResult;
import rx.Observable;
import rx.Single;

import static org.dizitart.no2.filters.Filters.and;
import static org.dizitart.no2.filters.Filters.eq;

public class NitriteApi extends Api {


    private final Document document;
    private final NitriteApiPort handler;

    public static class NitriteApiPort extends NitritePort implements ApiPort {


        public NitriteApiPort(NitriteDbs nitriteDbs) {
            super(nitriteDbs);
        }

        @Override
        protected NitriteCollection getCollection() {
            return nitriteDbs.getCollection(Schemas.Apis.collection());
        }

        @Override
        public Observable<Integer> deleteByIdProject(String idProject) {
            return Observable.fromCallable(
                    () -> getCollection().remove(and(
                            eq(Schemas.Apis.idProject.name(), idProject)
                    ))
            )
                    .map(WriteResult::getAffectedCount);
        }

        @Override
        public Api newInstance(String method, String path, String idProject) {
            Document document = Document.createDocument(Schemas.Apis.method.name(), method)
                    .put(Schemas.Apis.path.name(), path)
                    .put(Schemas.Apis.idProject.name(), idProject);
            return new NitriteApi(this, method, path, idProject, document);
        }

        @Override
        public Observable<Api> findAll() {
            return Observable.from(getCollection().find().toList())
                    .map(doc -> {
                        String method = doc.get(Schemas.Apis.method.name(), String.class);
                        String path = doc.get(Schemas.Apis.path.name(), String.class);
                        String idProject = doc.get(Schemas.Apis.idProject.name(), String.class);
                        return new NitriteApi(this, method, path, idProject, doc);
                    });
        }


        public Single<Boolean> create(NitriteApi nitriteApi) {
            return Single.fromCallable(() -> {
                getCollection().update(nitriteApi.document, true);
                return true;
            });
        }
    }

    private NitriteApi(NitriteApiPort nitriteApiHandler, String method, String path, String idProject, Document document) {
        super(method, path, idProject);
        this.handler = nitriteApiHandler;
        this.document = document;
    }

    @Override
    public String id() {
        return document.get(Schemas.Apis.id.name(), String.class);
    }

    @Override
    public Api setId(String newId) {
        document.put(Schemas.Apis.id.name(), newId);
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
        return this.handler.create(this);
    }

    @Override
    public String name() {
        return document.get(Schemas.Apis.name.name(), String.class);
    }

    @Override
    public String artifactId() {
        return document.get(Schemas.Apis.artifactId.name(), String.class);
    }

    @Override
    public String groupId() {
        return document.get(Schemas.Apis.groupId.name(), String.class);
    }

    @Override
    public String returns() {
        return document.get(Schemas.Apis.returns.name(), String.class);
    }

    @Override
    public String params() {
        return document.get(Schemas.Apis.params.name(), String.class);
    }

    @Override
    public String comment() {
        return document.get(Schemas.Apis.comment.name(), String.class);
    }

    @Override
    public String since() {
        return document.get(Schemas.Apis.since.name(), String.class);
    }

    @Override
    public String className() {
        return document.get(Schemas.Apis.className.name(), String.class);
    }

    @Override
    public long latestUpdate() {
        return document.get(Schemas.Apis.latestUpdate.name(), Long.class);
    }
}
