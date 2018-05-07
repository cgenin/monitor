package net.christophe.genin.domain.server.model;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.adapter.nitrite.NitriteRaw;
import net.christophe.genin.domain.server.command.Treatments;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public interface Raw {


    JsonObject json();

    String artifactId();


    Single<Boolean> updateState(Treatments treatments);

    long update();

    /**
     * Create an new Raw Event.
     *
     * @param object the raw datas.
     * @return The id of the created event.
     */
    static Single<Long> save(JsonObject object) {
        return new NitriteRaw().save(object)
                .observeOn(Schedulers.io());
    }

    static Single<Integer> updateAllStatesBy(Treatments treatments) {
        return new NitriteRaw().updateAllStatesBy(treatments);
    }

    static Observable<Raw> findByStateFirst(Treatments treatments) {
        return new NitriteRaw().findByStateFirst(treatments);
    }

}
