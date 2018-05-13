package net.christophe.genin.monitor.domain.server.model.port;

import net.christophe.genin.monitor.domain.server.model.Dependency;
import rx.Observable;
import rx.Single;

public interface DependencyPort {
    Single<Integer> removeByUsedBy(String usedBy);

    Single<Boolean> create(String resource, String usedBy);

    Single<Integer> removeAll();

    Observable<String> findAllResource();

    Observable<String> usedBy(String resource);

    Observable<Dependency> findAll();
}
