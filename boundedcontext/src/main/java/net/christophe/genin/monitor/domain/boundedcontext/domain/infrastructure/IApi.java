package net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure;

import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Api;
import rx.Observable;
import rx.Single;

/**
 * Port for Api.
 */
public interface IApi {
    /**
     * Delete all api from an specific id project.
     * @param idProject the id.
     * @return the number of deleted row's
     */
    Observable<Integer> deleteByIdProject(String idProject);

    /**
     * Create an new instance.
     * @param method the method of the pai.
     * @param path the path of the api.
     * @param idProject the id project.
     * @return the new instance.
     */
    Api newInstance(String method, String path, String idProject);

    /**
     * Suppress all api's store.
     * @return the number of row deleted.
     */
    Single<Integer> removeAll();

    /**
     * retreive all apis.
     * @return an observable from Api.
     */
    Observable<Api> findAll();
}
