package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitritePort;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Dependency;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IDependency;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.WriteResult;
import rx.Observable;
import rx.Single;

import static org.dizitart.no2.filters.Filters.eq;

public class NitriteDependency extends Dependency {

    private NitriteDependency(String resource, String usedBy) {
        super(resource, usedBy);
    }

    public static class NitriteIDependency extends NitritePort implements IDependency {


        public NitriteIDependency(NitriteDbs nitriteDbs) {
            super(nitriteDbs);
        }

        @Override
        public Single<Integer> removeByUsedBy(String usedBY) {
            return Single.fromCallable(() -> getCollection().remove(eq("usedBy", usedBY)))
                    .map(WriteResult::getAffectedCount);
        }

        @Override
        protected NitriteCollection getCollection() {
            return nitriteDbs.getCollection("dependencies");
        }

        @Override
        public Single<Boolean> create(String resource, String usedBY) {
            return Single.fromCallable(() -> {
                        Document doc = Document.createDocument("resource", resource)
                                .put("usedBy", usedBY);
                        getCollection().update(doc, true);
                        return true;
                    }
            );
        }

        @Override
        public Observable<String> findAllResource() {
            return Observable.from(getCollection().find().toList())
                    .map(doc -> doc.get("resource", String.class));
        }

        @Override
        public Observable<String> usedBy(String resource) {
            return Observable.from(getCollection().find(eq("resource", resource)).toList())
                    .map(doc -> doc.get("usedBy", String.class));
        }

        @Override
        public Observable<Dependency> findAll() {
            return Observable.from(getCollection().find().toList())
                    .map(doc -> new NitriteDependency(
                            doc.get("resource", String.class),
                            doc.get("usedBy", String.class)
                    ));
        }


    }


}
