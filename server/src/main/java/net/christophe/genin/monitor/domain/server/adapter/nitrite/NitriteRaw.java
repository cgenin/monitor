package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.command.Treatments;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.model.Raw;
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

    public Single<Long> save(JsonObject object) {
        return Single.fromCallable(() -> {
            final Document document = toDoc(object)
                    .put("state", Treatments.PROJECTS.getState());

            getCollection().insert(document);
            return document.getId().getIdValue();
        });
    }

    private static Raw toRaw(Document doc) {
        return new RawImpl(doc);
    }


    public Observable<Raw> findByStateFirst(Treatments treatments) {
        return findAllByState(treatments)
                .take(1);
    }

    public Observable<Raw> findAllByState(Treatments treatments) {
        return Observable.fromCallable(() -> getCollection()
                .find(eq(Schemas.RAW_STATE, treatments.getState()))
                .toList())
                .flatMap(Observable::from)
                .map(NitriteRaw::toRaw);
    }

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

    public static NitriteCollection getCollection() {
        return NitriteDbs.instance.getCollection(Schemas.RAW_COLLECTION);
    }

    public Observable<Raw> findAll() {
        return Observable.from(getCollection().find().toList())
                .map(NitriteRaw::toRaw);
    }

    public Single<Boolean> removeById(long id) {
        return Single.fromCallable(() -> getCollection()
                .remove(new RemoveByIdNititeFilter(id)))
                .subscribeOn(Schedulers.io())
                .map(wr -> wr.getAffectedCount() == 1);
    }

    public static class RawImpl implements Raw {
        private final Document document;
        private final JsonObject json;

        private RawImpl(Document document) {
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
        public long update() {
            return json.getLong(Schemas.Raw.update.name());
        }

        @Override
        public long id() {
            return document.getId().getIdValue();
        }

        @Override
        public Single<Boolean> updateState(Treatments treatments) {
            return Single.fromCallable(() -> {
                getCollection().update(document.put(Schemas.RAW_STATE, treatments.getState()), true);
                return true;
            });
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
