package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.model.Table;

import net.christophe.genin.monitor.domain.server.model.port.TablePort;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Observable;
import rx.Single;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.dizitart.no2.filters.Filters.and;
import static org.dizitart.no2.filters.Filters.eq;

public class NitriteTable extends Table {
    private static final Logger logger = LoggerFactory.getLogger(NitriteTable.class);

    private final NitriteTablePort handler;

    private NitriteTable(NitriteTablePort handler) {
        this.handler = handler;
    }


    public static class NitriteTablePort implements TablePort {

        private final NitriteDbs nitriteDbs;

        public NitriteTablePort(NitriteDbs nitriteDbs) {
            this.nitriteDbs = nitriteDbs;
        }


        private NitriteCollection getCollection() {
            return nitriteDbs.getCollection(Schemas.Tables.collection());

        }

        public Observable<Set<String>> findByService(String artifactId) {
            List<Document> documents = getCollection()
                    .find(eq(Schemas.Tables.services.name(), artifactId)).toList();
            return Observable.from(documents)
                    .map(doc -> doc.get(Schemas.Tables.name.name()).toString())
                    .reduce(new HashSet<>(), (acc, name) -> {
                        acc.add(name);
                        return acc;
                    });
        }

        @Override
        public Table newInstance() {
            return new NitriteTable(this);

        }

        @Override

        public Single<Boolean> remove(String tableName, String artifactId) {
            return Single.fromCallable(() -> getCollection()
                    .remove(
                            and(
                                    eq(Schemas.Tables.services.name(), artifactId),
                                    eq(Schemas.Tables.name.name(), tableName)

                            )
                    )).map(res -> res.getAffectedCount() == 1);
        }

        public Single<Boolean> create(Document document) {
            return Single.fromCallable(() -> {
                logger.info("New data for " + document.getId().getIdValue() + ". Document must be updated.");
                getCollection().update(document, true);
                return true;
            });
        }

        @Override

        public Single<Integer> removeAll() {
            NitriteCollection collection = getCollection();
            return NitriteDbs.removeAll(collection);
        }

        @Override

        public Observable<Table> findAll() {
            return Observable.from(getCollection().find().toList())
                    .map(doc -> new NitriteTable(this)
                            .setId(doc.get(Schemas.Tables.id.name(), String.class))
                            .setTableName(doc.get(Schemas.Tables.name.name(), String.class))
                            .setService(doc.get(Schemas.Tables.services.name(), String.class))
                            .setLastUpdated(doc.get(Schemas.Tables.latestUpdate.name(), Long.class)));
        }

        @Override

        public Observable<HashMap<String, Long>> countTablesByProjects() {
            return Observable.from(getCollection().find().toList())
                    .reduce(new HashMap<>(), (hashmap, doc) -> {
                        String service = doc.get(Schemas.Tables.services.name(), String.class);
                        long old = hashmap.getOrDefault(service, 0L);
                        hashmap.put(service, old + 1);
                        return hashmap;
                    });
        }
    }


    @Override
    public Single<Boolean> create() {
        Document document = Document.createDocument(Schemas.Projects.latestUpdate.name(), 0L)
                .put(Schemas.Tables.name.name(), tableName())
                .put(Schemas.Tables.id.name(), NitriteDbs.newId())
                .put(Schemas.Tables.services.name(), service())
                .put(Schemas.Tables.latestUpdate.name(), lastUpdated());

        return handler.create(document);
    }
}
