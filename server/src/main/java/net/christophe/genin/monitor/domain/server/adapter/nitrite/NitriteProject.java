package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.model.Project;
import net.christophe.genin.monitor.domain.server.model.port.ProjectPort;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Observable;
import rx.Single;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.dizitart.no2.filters.Filters.eq;


public class NitriteProject {


    private static class ProjectImpl implements Project {

        private final NitriteProjectPort handler;

        private final Document document;

        private ProjectImpl(NitriteProjectPort NitriteProjectHandler, Document document) {
            this.document = document;
            this.handler = NitriteProjectHandler;
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
        public List<String> javaDeps() {
            return new NitriteDbs.Attributes(document).toList(Schemas.Projects.javaDeps.name());
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
            return handler.save(this);
        }

        @Override
        public String id() {
            return document.get(Schemas.Projects.id.name(), String.class);
        }

        @Override
        public String release() {
            return document.get(Schemas.Projects.release.name(), String.class);
        }

        @Override
        public String snapshot() {
            return document.get(Schemas.Projects.snapshot.name(), String.class);
        }

        @Override
        public List<String> tables() {
            return new NitriteDbs.Attributes(document).toList(Schemas.Projects.tables.name());

        }

        @Override
        public List<String> apis() {
            return new NitriteDbs.Attributes(document).toList(Schemas.Projects.apis.name());

        }

        @Override
        public String changelog() {
            return document.get(Schemas.Projects.snapshot.name(), String.class);
        }
    }

    public static class NitriteProjectPort implements ProjectPort {

        private final NitriteDbs nitriteDbs;

        public NitriteProjectPort(NitriteDbs nitriteDbs) {
            this.nitriteDbs = nitriteDbs;
        }

        Project toProject(Document document) {
            return new ProjectImpl(this, document);
        }


        private NitriteCollection getCollection() {
            return nitriteDbs.getCollection(Schemas.Projects.collection());
        }


        @Override
        public Single<Project> readByName(String artifactId) {
            return Single.just(Optional.ofNullable(getCollection()
                    .find(eq(Schemas.Projects.name.name(), artifactId))
                    .firstOrDefault()
            )
                    .map(this::toProject)
                    .orElseGet(
                            () -> toProject(Document.createDocument(Schemas.Projects.latestUpdate.name(), 0L)
                                    .put(Schemas.Projects.id.name(), NitriteDbs.newId()))
                    ));
        }

        public Single<Boolean> save(ProjectImpl project) {
            String id = Optional.ofNullable(project.id())
                    .orElseGet(() -> UUID.randomUUID().toString());
            project.document.put(Schemas.Projects.id.name(), id);
            getCollection().update(project.document, true);
            return Single.just(true);
        }

        @Override
        public Single<Integer> removeAll() {
            return NitriteDbs.removeAll(getCollection());

        }

        @Override
        public Observable<Project> findAll() {
            return Observable.from(getCollection().find().toList())
                    .map(this::toProject);
        }
    }

}
