package net.christophe.genin.domain.server.adapter.nitrite;

import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.model.Api;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.WriteResult;
import rx.Observable;
import rx.Single;

import static org.dizitart.no2.filters.Filters.and;
import static org.dizitart.no2.filters.Filters.eq;

public class NitriteApi extends Api {


    private final Document document;

    private static NitriteCollection getCollection() {
        return Dbs.instance.getCollection(Schemas.Apis.collection());
    }

    public static Observable<Integer> deleteByIdProject(String idProject) {
        return Observable.fromCallable(
                () -> getCollection().remove(and(
                        eq(Schemas.Apis.idProject.name(), idProject)
                ))
        )
                .map(WriteResult::getAffectedCount);
    }

    public static Api newInstance(String method, String path, String idProject) {
        Document document = Document.createDocument(Schemas.Apis.method.name(), method)
                .put(Schemas.Apis.path.name(), path)
                .put(Schemas.Apis.idProject.name(), idProject);
        return new NitriteApi(method, path, idProject, document);
    }

    private NitriteApi(String method, String path, String idProject, Document document) {
        super(method, path, idProject);
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
        return Single.fromCallable(() -> {
            getCollection().update(document, true);
            return true;
        });
    }
}
