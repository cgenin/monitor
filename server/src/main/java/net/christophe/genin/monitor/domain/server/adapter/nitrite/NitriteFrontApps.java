package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.model.FrontApps;
import net.christophe.genin.monitor.domain.server.model.port.FrontAppsPort;
import org.dizitart.no2.Document;
import org.dizitart.no2.Filter;
import org.dizitart.no2.NitriteCollection;

import static org.dizitart.no2.filters.Filters.*;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class NitriteFrontApps extends FrontApps {
    private final NitriteFrontAppsPort port;

    public static class NitriteFrontAppsPort implements FrontAppsPort {


        private final NitriteCollection collection;

        public NitriteFrontAppsPort(NitriteDbs instance) {
            this.collection = instance.getCollection("frontApps");
        }

        @Override
        public FrontApps newInstance(JsonObject packagesJson, String groupId, String artifactId, String version) {
            return new NitriteFrontApps(this, packagesJson, groupId, artifactId, version, new Date().getTime());
        }

        @Override
        public Observable<FrontApps> findBy(String groupId, String artifactId, String version) {

            return load(groupId, artifactId, version)
                    .flatMap(doc -> {
                        if (Objects.isNull(doc.get("id"))) {
                            return Observable.empty();
                        }
                        return Observable.just(convert(doc));
                    });
        }

        private Observable<Document> load(String groupId, String artifactId, String version) {
            return Observable.fromCallable(
                    () -> {
                        Filter filter = and(
                                eq("groupId", groupId), eq("artifactId", artifactId), eq("version", version)
                        );
                        return collection.find(filter)
                                .firstOrDefault();
                    }
            ).subscribeOn(Schedulers.io());
        }

        private FrontApps convert(Document doc) {
            JsonObject packagesJson = Optional.ofNullable(doc.get("data", String.class))
                    .map(JsonObject::new)
                    .orElse(new JsonObject());
            String groupId = doc.get("groupId", String.class);
            String artifactId = doc.get("artifactId", String.class);
            String version = doc.get("version", String.class);
            Long latestUpdate = doc.get("latestUpdate", Long.class);
            String id = doc.get("id", String.class);
            return new NitriteFrontApps(this, packagesJson, groupId, artifactId, version, latestUpdate)
                    .setId(id);
        }

        public Single<Boolean> save(NitriteFrontApps fa) {
            return load(fa.groupId(), fa.artifactId(), fa.version())
                    .toSingle()
                    .map(doc -> doc
                            .put("groupId", fa.groupId())
                            .put("artifactId", fa.artifactId())
                            .put("version", fa.version()).put("latestUpdate", fa.lastUpdate())
                            .put("id", fa.id())
                            .put("data", fa.packagesJson().encode()))
                    .map(doc -> collection.update(doc, true))
                    .map(res -> (res.getAffectedCount() == 1));
        }

        @Override
        public Observable<FrontApps> findAll() {
            return Observable.fromCallable(()-> collection.find().toList())
                    .subscribeOn(Schedulers.io())
                    .flatMap(Observable::from)
                    .map(this::convert);
        }
    }

    private NitriteFrontApps(NitriteFrontAppsPort port, JsonObject packagesJson, String groupId, String artifactId, String version, long lastUpdate) {
        super(packagesJson, groupId, artifactId, version, lastUpdate);
        this.port = port;
    }

    @Override
    public Single<Boolean> save() {
        return Single.just(this)
                .map(fa -> {
                    if (Objects.isNull(fa.id())) {
                        String newId = UUID.randomUUID().toString();
                        fa.setId(newId);
                        return fa;
                    }
                    return fa;
                })
                .flatMap(port::save);
    }
}
