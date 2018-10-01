package net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.boundedcontext.domain.Treatments;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Raw;
import rx.Observable;
import rx.Single;

public interface IRaw {

    Single<Long> save(JsonObject object);


    Single<Integer> updateAllStatesBy(Treatments treatments);

    Observable<Raw> findByStateFirst(Treatments treatments);

    Observable<Raw> findAllByState(Treatments treatments);

    Observable<Raw> findAll();

    Single<Boolean> removeById(long id);
}
