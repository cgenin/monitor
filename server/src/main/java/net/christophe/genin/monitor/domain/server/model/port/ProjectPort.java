package net.christophe.genin.monitor.domain.server.model.port;

import net.christophe.genin.monitor.domain.server.model.Project;
import rx.Observable;
import rx.Single;

/**
 * Port for project.
 */
public interface ProjectPort {
    /**
     * Find a project by name.
     * @param artifactId the name.
     * @return the Project.
     */
    Single<Project> readByName(String artifactId);

    /**
     * Suppress all projects.
     * @return
     */
    Single<Integer> removeAll();

    /**
     * retreive all projects.
     * @return An observable of Project.
     */
    Observable<Project> findAll();
}
