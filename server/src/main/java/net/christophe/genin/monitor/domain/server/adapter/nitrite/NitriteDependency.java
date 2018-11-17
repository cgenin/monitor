package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitritePort;
import net.christophe.genin.monitor.domain.server.model.Dependency;
import net.christophe.genin.monitor.domain.server.model.port.DependencyPort;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.WriteResult;
import rx.Observable;
import rx.Single;

import static org.dizitart.no2.filters.Filters.eq;

public class NitriteDependency extends Dependency {

    public NitriteDependency(String resource, String usedBy) {
        super(resource, usedBy);
    }

    public static class NitriteDependencyPort extends NitritePort implements DependencyPort {


        public NitriteDependencyPort(NitriteDbs nitriteDbs) {
            super(nitriteDbs);
        }

        @Override
        public Single<Integer> removeByUsedBy(String usedBY) {
            return Single.fromCallable(() -> getCollection().remove(eq(Schemas.Dependency.usedBy.name(), usedBY)))
                    .map(WriteResult::getAffectedCount);
        }

        @Override
        protected NitriteCollection getCollection() {
            return nitriteDbs.getCollection(Schemas.Dependency.collection());
        }

        @Override
        public Single<Boolean> create(String resource, String usedBY) {
            return Single.fromCallable(() -> {
                        Document doc = Document.createDocument(Schemas.Dependency.resource.name(), resource)
                                .put(Schemas.Dependency.usedBy.name(), usedBY);
                        getCollection().update(doc, true);
                        return true;
                    }
            );
        }

        @Override
        public Observable<String> findAllResource() {
            return Observable.from(getCollection().find().toList())
                    .map(doc -> doc.get(Schemas.Dependency.resource.name(), String.class));
        }

        @Override
        public Observable<String> usedBy(String resource) {
            return Observable.from(getCollection().find(eq(Schemas.Dependency.resource.name(), resource)).toList())
                    .map(doc -> doc.get(Schemas.Dependency.usedBy.name(), String.class));
        }

        @Override
        public Observable<Dependency> findAll() {
            return Observable.from(getCollection().find().toList())
                    .map(doc -> new NitriteDependency(
                            doc.get(Schemas.Dependency.resource.name(), String.class),
                            doc.get(Schemas.Dependency.usedBy.name(), String.class)
                    ));
        }


    }


}
