package net.christophe.genin.monitor.domain.server.model;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import net.christophe.genin.monitor.domain.server.command.Treatments;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Model representing an Raw events of an service domain.
 */
public interface Raw {

    static Observable<Raw> findAll() {
        return Adapters.get().rawHandler().findAll();
    }

    JsonObject json();

    Treatments state();

    String artifactId();

    Long update();

    Boolean archive();

    Long id();

    Single<Boolean> updateState(Treatments treatments);

    /**
     * Create an new Raw Event.
     *
     * @param object the raw datas.
     * @return The id of the created event.
     */
    static Single<Long> save(JsonObject object) {
        return Adapters.get().rawHandler().save(object)
                .observeOn(Schedulers.io());
    }

    static Single<Integer> updateAllStatesBy(Treatments treatments) {
        return Adapters.get().rawHandler().updateAllStatesBy(treatments);
    }

    static Observable<Raw> findByStateFirst(Treatments treatments) {
        return Adapters.get().rawHandler().findByStateFirst(treatments);
    }

    static Observable<Raw> findAllByState(Treatments treatments) {
        return Adapters.get().rawHandler().findAllByState(treatments);
    }

    static Single<Boolean> removeById(long id) {
        return Adapters.get().rawHandler().removeById(id);
    }


}
