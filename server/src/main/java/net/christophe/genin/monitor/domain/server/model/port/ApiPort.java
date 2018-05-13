package net.christophe.genin.monitor.domain.server.model.port;

import net.christophe.genin.monitor.domain.server.model.Api;
import rx.Observable;
import rx.Single;

public interface ApiPort {
    Observable<Integer> deleteByIdProject(String idProject);

    Api newInstance(String method, String path, String idProject);

    Single<Integer> removeAll();

    Observable<Api> findAll();
}
