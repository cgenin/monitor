package net.christophe.genin.monitor.domain.server.model.port;

import net.christophe.genin.monitor.domain.server.model.Version;
import rx.Observable;
import rx.Single;

public interface VersionPort {
    Single<Version> findByNameAndProjectOrDefault(String version, String idProject);

    Single<Integer> removeAll();

    Observable<Version> findByProject(String idProject);

    Observable<Version> findAll();
}
