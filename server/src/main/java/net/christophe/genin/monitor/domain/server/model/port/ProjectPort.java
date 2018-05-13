package net.christophe.genin.monitor.domain.server.model.port;

import net.christophe.genin.monitor.domain.server.model.Project;
import rx.Observable;
import rx.Single;

public interface ProjectPort {
    Single<Project> readByName(String artifactId);


    Single<Integer> removeAll();

    Observable<Project> findAll();
}
