package net.christophe.genin.domain.server.model.handler;

import net.christophe.genin.domain.server.model.Dependency;
import rx.Observable;
import rx.Single;

public interface DependencyHandler {
    Single<Integer> removeByUsedBy(String usedBy);

    Single<Boolean> create(String resource, String usedBy);

    Single<Integer> removeAll();

    Observable<String> findAllResource();

    Observable<String> usedBy(String resource);

    Observable<Dependency> findAll();
}
