package net.christophe.genin.monitor.domain.boundedcontext.domain;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IRaw;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Raw;
import net.christophe.genin.monitor.domain.boundedcontext.domain.utils.Schemas;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.Date;

public class RawDomain {

    private final IRaw rawDao;

    public RawDomain(IRaw rawDao) {
        this.rawDao = rawDao;

    }

    public Observable<Raw> findAll() {
        return rawDao.findAll();
    }

    /**
     * Create an new Raw Event.
     *
     * @param object the raw datas.
     * @return The id of the created event.
     */
    public Single<Long> save(JsonObject object) {
        long time = new Date().getTime();
        final JsonObject body = object.put(Schemas.Raw.update.name(), time);
        return rawDao.save(body)
                .observeOn(Schedulers.io());
    }

    public Single<Integer> updateAllStatesBy(Treatments treatments) {
        return rawDao.updateAllStatesBy(treatments);
    }

    public Observable<Raw> findByStateFirst(Treatments treatments) {
        return rawDao.findByStateFirst(treatments);
    }

    public Observable<Raw> findAllByState(Treatments treatments) {
        return rawDao.findAllByState(treatments);
    }

    public Single<Boolean> removeById(long id) {
        return rawDao.removeById(id);
    }


    public Single<Integer> reset() {
        return this.updateAllStatesBy(Treatments.PROJECTS);
    }


}

