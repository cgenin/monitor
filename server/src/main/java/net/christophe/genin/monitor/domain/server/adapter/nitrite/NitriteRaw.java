package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.command.Treatments;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.model.Raw;
import net.christophe.genin.monitor.domain.server.model.port.RawPort;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.filters.BaseFilter;
import org.dizitart.no2.store.NitriteMap;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.Collections;
import java.util.Set;

import static org.dizitart.no2.filters.Filters.eq;

/**
 * Persistance layer for Raw with nitrite.
 */
public class NitriteRaw {

    public static Document toDoc(JsonObject json) {
        return Document.createDocument("data", json.encode());
    }

    public static JsonObject toJson(Document dc) {
        final String data = dc.get("data").toString();
        return new JsonObject(data);
    }


    public static class NitriteRawPort implements RawPort {

        private final NitriteDbs dbs;

        public NitriteRawPort(NitriteDbs dbs) {
            this.dbs = dbs;
        }

        private Raw toRaw(Document doc) {
            return new RawImpl(this, doc);
        }

        public NitriteCollection getCollection() {
            return dbs.getCollection(Schemas.RAW_COLLECTION);
        }

        @Override
        public Single<Long> save(JsonObject object) {
            return Single.fromCallable(() -> {
                final Document document = toDoc(object)
                        .put("state", Treatments.PROJECTS.getState());

                getCollection().insert(document);
                return document.getId().getIdValue();
            }).subscribeOn(Schedulers.io());
        }

        public Single<Boolean> updateState(RawImpl raw, Treatments treatments) {
            return Single.fromCallable(() -> {
                getCollection().update(raw.document.put(Schemas.RAW_STATE, treatments.getState()), true);
                return true;
            });
        }

        @Override
        public Single<Integer> updateAllStatesBy(Treatments treatments) {
            NitriteCollection collection = getCollection();
            return Observable.from(collection.find().toList())
                    .subscribeOn(Schedulers.computation())
                    .map(doc -> {
                                Document put = doc.put(Schemas.RAW_STATE, treatments.getState());
                                collection.update(put, true);
                                return 1;
                            }
                    )
                    .reduce(0, (acc, updated) -> acc + updated)
                    .toSingle();
        }

        @Override
        public Observable<Raw> findByStateFirst(Treatments treatments) {
            return getCollection()
                    .find(eq(Schemas.RAW_STATE, treatments.getState()))
                    .toList()
                    .stream()
                    .findFirst()
                    .map(this::toRaw)
                    .map(Observable::just)
                    .orElse(Observable.empty());
        }

        @Override
        public Observable<Raw> findAllByState(Treatments treatments) {
            return Observable.fromCallable(() -> getCollection()
                    .find(eq(Schemas.RAW_STATE, treatments.getState()))
                    .toList())
                    .flatMap(Observable::from)
                    .map(this::toRaw);
        }


        @Override
        public Observable<Raw> findAll() {
            return Observable.from(getCollection().find().toList())
                    .map(this::toRaw);
        }

        @Override
        public Single<Boolean> removeById(long id) {
            return Single.fromCallable(() -> getCollection()
                    .remove(new RemoveByIdNititeFilter(id)))
                    .subscribeOn(Schedulers.io())
                    .map(wr -> wr.getAffectedCount() == 1);
        }
    }


    public static class RawImpl implements Raw {

        private final NitriteRawPort handler;
        private final Document document;
        private final JsonObject json;

        private RawImpl(NitriteRawPort handler, Document document) {
            this.handler = handler;
            this.document = document;
            this.json = toJson(document);
        }

        @Override
        public String artifactId() {
            return json.getString(Schemas.Raw.artifactId.name());
        }

        @Override
        public JsonObject json() {
            return json;
        }

        @Override
        public Treatments state() {
            return Treatments.parse(document.get(Schemas.RAW_STATE, Integer.class));
        }

        @Override
        public Long update() {
            return json.getLong(Schemas.Raw.update.name(), 0L);
        }


        @Override
        public Long id() {
            return document.getId().getIdValue();
        }

        @Override
        public Single<Boolean> updateState(Treatments treatments) {
            return handler.updateState(this, treatments);
        }
    }

    private static class RemoveByIdNititeFilter extends BaseFilter {

        private final long id;

        private RemoveByIdNititeFilter(long ids) {
            this.id = ids;
        }

        @Override
        public Set<NitriteId> apply(NitriteMap<NitriteId, Document> nitriteMap) {

            return nitriteMap.keySet().parallelStream()
                    .filter(nid -> nid.getIdValue() == id)
                    .findAny()
                    .map(Collections::singleton)
                    .orElseGet(Collections::emptySet);
        }
    }
}
