package net.christophe.genin.monitor.domain.server.model.port;

import net.christophe.genin.monitor.domain.server.model.Version;
import rx.Observable;
import rx.Single;

/**
 * Port for Version model.
 */
public interface VersionPort {
    /**
     * Find an specific Version.
     *
     * @param version   the version name.
     * @param idProject the id of the project.
     * @return the Version if found.
     */
    Single<Version> findByNameAndProjectOrDefault(String version, String idProject);

    /**
     * Suppress all Versions.
     *
     * @return the number of deleted rows.
     */
    Single<Integer> removeAll();

    /**
     * Find all Version of an specific project.
     *
     * @param idProject the id project.
     * @return An observable of Version.
     */
    Observable<Version> findByProject(String idProject);

    /**
     * Find all versions.
     *
     * @return An observable of versions.
     */
    Observable<Version> findAll();
}
