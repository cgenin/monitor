package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitritePort;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Api;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IApi;
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

    public static class NitriteApiPort extends NitritePort implements IApi {


        public NitriteApiPort(NitriteDbs nitriteDbs) {
            super(nitriteDbs);
        }

        @Override
        protected NitriteCollection getCollection() {
            return nitriteDbs.getCollection("apis");
        }

        @Override
        public Observable<Integer> deleteByIdProject(String idProject) {
            return Observable.fromCallable(
                    () -> getCollection().remove(and(
                            eq("idProject", idProject)
                    ))
            )
                    .map(WriteResult::getAffectedCount);
        }

        @Override
        public Api newInstance(String method, String path, String idProject) {
            Document document = Document.createDocument("method", method)
                    .put("path", path)
                    .put("idProject", idProject);
            return new NitriteApi(this, method, path, idProject, document);
        }

        @Override
        public Observable<Api> findAll() {
            return Observable.from(getCollection().find().toList())
                    .map(doc -> {
                        String method = doc.get("method", String.class);
                        String path = doc.get("path", String.class);
                        String idProject = doc.get("idProject", String.class);
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
        return document.get("id", String.class);
    }

    @Override
    public Api setId(String newId) {
        document.put("id", newId);
        return this;
    }

    @Override
    public Api setArtifactId(String artifactId) {
        document.put("artifactId", artifactId);
        return this;
    }

    @Override
    public Api setGroupId(String groupId) {
        document.put("groupId", groupId);
        return this;
    }

    @Override
    public Api setSince(String version) {
        document.put("since", version);
        return this;
    }

    @Override
    public Api setLatestUpdate(long update) {
        document.put("latestUpdate", update);
        return this;
    }

    @Override
    public Api setName(String name) {
        document.put("name", name);
        return this;
    }

    @Override
    public Api setReturns(String returns) {
        document.put("returns", returns);
        return this;
    }

    @Override
    public Api setParams(String params) {
        document.put("params", params);
        return this;
    }

    @Override
    public Api setComment(String comment) {
        document.put("comment", comment);
        return this;
    }

    @Override
    public Api setClassName(String className) {
        document.put("className", className);
        return this;
    }

    @Override
    public Single<Boolean> create() {
        return this.handler.create(this);
    }

    @Override
    public String name() {
        return document.get("name", String.class);
    }

    @Override
    public String artifactId() {
        return document.get("artifactId", String.class);
    }

    @Override
    public String groupId() {
        return document.get("groupId", String.class);
    }

    @Override
    public String returns() {
        return document.get("returns", String.class);
    }

    @Override
    public String params() {
        return document.get("params", String.class);
    }

    @Override
    public String comment() {
        return document.get("comment", String.class);
    }

    @Override
    public String since() {
        return document.get("since", String.class);
    }

    @Override
    public String className() {
        return document.get("className", String.class);
    }

    @Override
    public long latestUpdate() {
        return document.get("latestUpdate", Long.class);
    }
}
