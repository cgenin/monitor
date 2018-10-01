package net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure;

import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Dependency;
import rx.Observable;
import rx.Single;

/**
 * The dependdencies Port.
 */
public interface IDependency {
    /**
     * Delete all dependencies used by an specific service.
     *
     * @param usedBy the name of the service.
     * @return the number of deleted rows.
     */
    Single<Integer> removeByUsedBy(String usedBy);

    /**
     * create an new  dependency.
     *
     * @param resource the lib
     * @param usedBy   the service which used this library.
     * @return true if created.
     */
    Single<Boolean> create(String resource, String usedBy);

    /**
     * Suppress all dependecies's store.
     *
     * @return the number of rows.
     */
    Single<Integer> removeAll();

    /**
     * Retreive all stored lib.
     *
     * @return an observable of their name.
     */
    Observable<String> findAllResource();

    /**
     * Get all library use by an specific service.
     *
     * @param resource the name of the service.
     * @return an observable of name of the domain.
     */
    Observable<String> usedBy(String resource);

    /**
     * Retreive all dependey.
     *
     * @return an observable of all dependencies.
     */
    Observable<Dependency> findAll();
}
