package net.christophe.genin.monitor.domain.server.model.port;

import net.christophe.genin.monitor.domain.server.model.Table;
import net.christophe.genin.monitor.domain.server.model.Table;
import rx.Observable;
import rx.Single;

import java.util.HashMap;
import java.util.Set;

public interface TablePort {
    Observable<Set<String>> findByService(String artifactId);

    Single<Boolean> remove(String tableName, String artifactId);

    Table newInstance();

    Single<Integer> removeAll();

    Observable<Table> findAll();

    Observable<HashMap<String, Long>> countTablesByProjects();
}
