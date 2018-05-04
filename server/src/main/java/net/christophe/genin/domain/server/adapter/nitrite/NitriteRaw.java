package net.christophe.genin.domain.server.adapter.nitrite;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.command.Treatments;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.model.Raw;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Observable;
import rx.Single;

import static org.dizitart.no2.filters.Filters.eq;

/**
 * Persistance layer for Raw with nitrite.
 */
public class NitriteRaw {

    public Single<Long> save(JsonObject object) {
        return Single.fromCallable(() -> {
            final Document document = Dbs.Raws.toDoc(object)
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

    public static NitriteCollection getCollection() {
        return Dbs.instance.getCollection(Schemas.RAW_COLLECTION);
    }

    public static class RawImpl implements Raw {
        private final Document document;
        private final JsonObject json;

        private RawImpl(Document document) {
            this.document = document;
            this.json = Dbs.Raws.toJson(document);
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
        public long update() {
            return json.getLong(Schemas.Raw.update.name());
        }

        @Override
        public Single<Boolean> updateState(Treatments treatments) {
            return Single.fromCallable(() -> {
                getCollection().update(document.put(Schemas.RAW_STATE, Treatments.TABLES.getState()));
                return true;
            });
        }
    }
}
