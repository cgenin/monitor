package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Table;

import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.ITable;
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
            return nitriteDbs.getCollection("tables");

        }

        public Observable<Set<String>> findByService(String artifactId) {
            List<Document> documents = getCollection()
                    .find(eq("services", artifactId)).toList();
            return Observable.from(documents)
                    .map(doc -> doc.get("name").toString())
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
                                    eq("services", artifactId),
                                    eq("name", tableName)

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
                            .setId(doc.get("id", String.class))
                            .setTableName(doc.get("name", String.class))
                            .setService(doc.get("services", String.class))
                            .setLastUpdated(doc.get("latestUpdate", Long.class)));
        }

        @Override

        public Observable<HashMap<String, Long>> countTablesByProjects() {
            return Observable.from(getCollection().find().toList())
                    .reduce(new HashMap<>(), (hashmap, doc) -> {
                        String service = doc.get("services", String.class);
                        long old = hashmap.getOrDefault(service, 0L);
                        hashmap.put(service, old + 1);
                        return hashmap;
                    });
        }
    }


    @Override
    public Single<Boolean> create() {
        Document document = Document.createDocument("latestUpdate", 0L)
                .put("name", tableName())
                .put("id", NitriteDbs.newId())
                .put("services", service())
                .put("latestUpdate", lastUpdated());

        return handler.create(document);
    }
}
