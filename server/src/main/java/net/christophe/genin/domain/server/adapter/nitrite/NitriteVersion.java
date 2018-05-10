package net.christophe.genin.domain.server.adapter.nitrite;

import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.model.Version;
import net.christophe.genin.domain.server.model.handler.VersionHandler;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;

import static org.dizitart.no2.filters.Filters.*;

import rx.Observable;
import rx.Single;

import java.util.List;
import java.util.Optional;

import static org.dizitart.no2.filters.Filters.and;

public class NitriteVersion extends Version {

    private final Document document;
    private final NitriteVersionHandler handler;


    private NitriteVersion(NitriteVersionHandler handler, String name, String idProject, Document document) {
        super(name, idProject);
        this.handler = handler;
        this.document = document;
    }

    @Override
    public String id() {
        return Optional.ofNullable(document.get(Schemas.Version.id.name()))
                .map(Object::toString)
                .orElseGet(Dbs::newId);
    }

    public long latestUpdate() {

        return Optional.ofNullable(document.get(Schemas.Version.latestUpdate.name(), Long.class))
                .orElse(0L);
    }

    public Version setLatestUpdate(long latestUpdate) {
        document.put(Schemas.Version.latestUpdate.name(), latestUpdate);
        return this;
    }

    @Override
    public Version setIsSnapshot(boolean snapshot) {
        document.put(Schemas.Version.isSnapshot.name(), snapshot);

        return this;
    }

    @Override
    public Version setJavadeps(List<String> javadeps) {
        document.put(Schemas.Version.javaDeps.name(), javadeps);
        return this;
    }

    @Override
    public Version setTables(List<String> tables) {
        document.put(Schemas.Version.tables.name(), tables);
        return this;
    }

    @Override
    public Version setChangeLog(String changeLog) {
        document.put(Schemas.Version.changelog.name(), changeLog);
        return this;
    }

    @Override
    public Version setApis(List<String> apis) {
        document.put(Schemas.Version.apis.name(), apis);
        return this;
    }

    @Override
    public Single<Boolean> save() {
        return handler.save(this);
    }

    @Override
    public boolean isSnapshot() {
        return document.get(Schemas.Version.isSnapshot.name(), Boolean.class);
    }

    @Override
    public String changelog() {
        return document.get(Schemas.Version.changelog.name(), String.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> tables() {
        return (List<String>) document.get(Schemas.Version.tables.name());

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> apis() {
        return (List<String>) document.get(Schemas.Version.apis.name());
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> javaDeps() {
        return (List<String>) document.get(Schemas.Version.javaDeps.name());
    }

    public static class NitriteVersionHandler implements VersionHandler {
        private final Dbs dbs;

        public NitriteVersionHandler(Dbs dbs) {
            this.dbs = dbs;
        }

        private NitriteCollection getCollection() {
            return dbs.getCollection(Schemas.Version.collection());
        }

        public Single<Version> findByNameAndProjectOrDefault(String version, String idProject) {

            return Single.fromCallable(
                    () -> getCollection().find(and(
                            eq(Schemas.Version.name.name(), version),
                            eq(Schemas.Version.idproject.name(), idProject)
                    )).toList())
                    .map(list -> {
                        if (list.isEmpty()) {
                            return Document.createDocument(Schemas.Version.latestUpdate.name(), 0L)
                                    .put(Schemas.Version.name.name(), version)
                                    .put(Schemas.Version.idproject.name(), idProject);
                        }
                        return list.get(0);
                    })
                    .map(this::toModel);

        }


        private Version toModel(Document document) {
            String version = document.get(Schemas.Version.name.name()).toString();
            String idproject = document.get(Schemas.Version.idproject.name()).toString();
            return new NitriteVersion(this, version, idproject, document);
        }

        private Single<Boolean> save(NitriteVersion version) {
            return Single.fromCallable(() -> {
                getCollection().update(version.document, true);
                return true;
            });
        }

        public Single<Integer> removeAll() {
            return Dbs.removeAll(getCollection());
        }

        public Observable<Version> findByProject(String idProject) {
            return Observable.from(getCollection().find(eq(Schemas.Version.idproject.name(), idProject)).toList())
                    .map(this::toModel);
        }

        @Override
        public Observable<Version> findAll() {
            return Observable.from(getCollection().find().toList())
                    .map(this::toModel);
        }
    }
}
