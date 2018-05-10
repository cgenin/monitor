package net.christophe.genin.domain.server.model.handler;

import net.christophe.genin.domain.server.adapter.nitrite.NitriteProject;
import net.christophe.genin.domain.server.model.Project;
import rx.Observable;
import rx.Single;

public interface ProjectHandler {
    Single<Project> readByName(String artifactId);


    Single<Integer> removeAll();

    Observable<Project> findAll();
}
