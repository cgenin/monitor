package net.christophe.genin.domain.server.adapter.nitrite;

import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.nitrite.NitriteHandler;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.WriteResult;
import rx.Single;

import static org.dizitart.no2.filters.Filters.eq;

public class NitriteDependency extends net.christophe.genin.domain.server.model.Dependency {

    public static class NitriteDependencyHandler extends NitriteHandler {


        public NitriteDependencyHandler(Dbs dbs) {
            super(dbs);
        }

        public Single<Integer> removeByUsedBy(String usedBY) {
            return Single.fromCallable(() -> getCollection().remove(eq(Schemas.Dependency.usedBy.name(), usedBY)))
                    .map(WriteResult::getAffectedCount);
        }

        @Override
        protected NitriteCollection getCollection() {
            return dbs.getCollection(Schemas.Dependency.collection());
        }

        public Single<Boolean> create(String resource, String usedBY) {
            return Single.fromCallable(() -> {
                        Document doc = Document.createDocument(Schemas.Dependency.resource.name(), resource)
                                .put(Schemas.Dependency.usedBy.name(), usedBY);
                        getCollection().update(doc, true);
                        return true;
                    }
            );
        }


    }


}
