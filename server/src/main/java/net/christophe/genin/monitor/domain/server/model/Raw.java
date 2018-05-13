package net.christophe.genin.monitor.domain.server.model;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.adapter.nitrite.NitriteRaw;
import net.christophe.genin.monitor.domain.server.command.Treatments;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

public interface Raw {


    static Observable<Raw> findAll() {
        return new NitriteRaw().findAll();
    }

    JsonObject json();

    Treatments state();

    String artifactId();

    long update();


    Single<Boolean> updateState(Treatments treatments);

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
