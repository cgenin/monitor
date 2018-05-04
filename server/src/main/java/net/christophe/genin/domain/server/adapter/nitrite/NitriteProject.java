package net.christophe.genin.domain.server.adapter.nitrite;

import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.model.Project;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Single;

import java.util.List;
import java.util.Optional;

import static org.dizitart.no2.filters.Filters.eq;

public class NitriteProject {
    public static Single<Project> readByName(String artifactId) {
        return Single.just(Optional.ofNullable(getCollection()
                .find(eq(Schemas.Projects.name.name(), artifactId))
                .firstOrDefault()
        )
                .map(NitriteProject::toProject)
                .orElseGet(
                        () -> toProject(Document.createDocument(Schemas.Projects.latestUpdate.name(), 0L)
                                .put(Schemas.Projects.id.name(), Dbs.newId()))
                ));
    }

    private static Project toProject(Document document) {
        return new ProjectImpl(document);
    }

    private static NitriteCollection getCollection() {
        return Dbs.instance.getCollection(Schemas.Projects.collection());
    }

    private static class ProjectImpl implements Project {

        private final Document document;

        private ProjectImpl(Document document) {
            this.document = document;
        }

        @Override
        public long latestUpdate() {
            return Long.valueOf(document.get(Schemas.Projects.latestUpdate.name()).toString());
        }

        @Override
        public String name() {
            return document.get(Schemas.Projects.name.name(), String.class);

        }

        @Override
        public Project setName(String artifactId) {
            document.put(Schemas.Projects.name.name(), artifactId);
            return this;
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
            getCollection().update(document, true);
            return Single.just(true);
        }
    }
}
