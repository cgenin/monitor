package net.christophe.genin.domain.server.model.handler;

import net.christophe.genin.domain.server.model.Api;
import rx.Observable;
import rx.Single;

public interface ApiHandler {
    Observable<Integer> deleteByIdProject(String idProject);

    Api newInstance(String method, String path, String idProject);

    Single<Integer> removeAll();

    Observable<Api> findAll();
}
