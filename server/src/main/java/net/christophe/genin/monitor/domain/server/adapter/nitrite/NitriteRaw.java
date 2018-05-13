package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.command.Treatments;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.model.Raw;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import static org.dizitart.no2.filters.Filters.eq;

/**
 * Persistance layer for Raw with nitrite.
 */
public class NitriteRaw {

    public Single<Long> save(JsonObject object) {
        return Single.fromCallable(() -> {
            final Document document = NitriteDbs.Raws.toDoc(object)
                    .put("state", Treatments.PROJECTS.getState());

            getCollection().insert(document);
            return document.getId().getIdValue();
        });
    }

    private static Raw toRaw(Document doc) {
        return new RawImpl(doc);
    }

    public Observable<Raw> findByStateFirst(Treatments treatments) {
        final NitriteCollection collection = getCollection();
        return collection
                .find(eq(Schemas.RAW_STATE, treatments.getState()))
                .toList()
                .stream()
                .findFirst()
                .map(NitriteRaw::toRaw)
                .map(Observable::just)
                .orElse(Observable.empty());
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

    public static class RawImpl implements Raw {
        private final Document document;
        private final JsonObject json;

        private RawImpl(Document document) {
            this.document = document;
            this.json = NitriteDbs.Raws.toJson(document);
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
        public Single<Boolean> updateState(Treatments treatments) {
            return Single.fromCallable(() -> {
                getCollection().update(document.put(Schemas.RAW_STATE, treatments.getState()), true);
                return true;
            });
        }
    }
}
