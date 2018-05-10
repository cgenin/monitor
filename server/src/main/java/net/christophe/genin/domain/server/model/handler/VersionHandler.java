package net.christophe.genin.domain.server.model.handler;

import net.christophe.genin.domain.server.model.Version;
import rx.Observable;
import rx.Single;

public interface VersionHandler {
    Single<Version> findByNameAndProjectOrDefault(String version, String idProject);

    Single<Integer> removeAll();

    Observable<Version> findByProject(String idProject);

    Observable<Version> findAll();
}
