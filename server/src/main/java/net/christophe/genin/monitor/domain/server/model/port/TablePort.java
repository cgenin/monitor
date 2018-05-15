package net.christophe.genin.monitor.domain.server.model.port;

import net.christophe.genin.monitor.domain.server.model.Table;
import rx.Observable;
import rx.Single;

import java.util.HashMap;
import java.util.Set;

/**
 * Port for Table.
 */
public interface TablePort {
    /**
     * find all table which contains an specific Service.
     * @param artifactId the name of the service.
     * @return An Observable with an set of table name.
     */
    Observable<Set<String>> findByService(String artifactId);

    /**
     * Suppress an specific table by table's name and service's name.
     * @param tableName the table's name.
     * @param artifactId the service's name.
     * @return true if deleted.
     */
    Single<Boolean> remove(String tableName, String artifactId);

    /**
     * Create an new instance.
     * @return the new instance.
     */
    Table newInstance();

    /**
     * Suppress all tables.
     * @return the number of rows deleted.
     */
    Single<Integer> removeAll();

    /**
     * Retreive all tables.
     * @return An observable of Table.
     */
    Observable<Table> findAll();

    /**
     * Count all table used by an service.
     * @return an hasmap which contains an key whith the service name and for value the count of used tables.
     */
    Observable<HashMap<String, Long>> countTablesByProjects();
}
