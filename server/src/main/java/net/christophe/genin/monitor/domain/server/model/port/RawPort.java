package net.christophe.genin.monitor.domain.server.model.port;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.adapter.nitrite.NitriteRaw;
import net.christophe.genin.monitor.domain.server.command.Treatments;
import net.christophe.genin.monitor.domain.server.model.Raw;
import rx.Observable;
import rx.Single;

public interface RawPort {

    Single<Long> save(JsonObject object);


    Single<Integer> updateAllStatesBy(Treatments treatments);

    Observable<Raw> findByStateFirst(Treatments treatments);

    Observable<Raw> findAllByState(Treatments treatments);

    Observable<Raw> findAll();

    Single<Boolean> removeById(long id);
}
