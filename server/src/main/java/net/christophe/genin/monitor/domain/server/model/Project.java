package net.christophe.genin.monitor.domain.server.model;

import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import rx.Observable;
import rx.Single;

import java.util.List;

public interface Project {

    static Single<Project> findByName(String artifactId) {
        return Adapters
                .get().projectHandler().readByName(artifactId);
    }

    static  Single<Integer> removeAll(){
        return Adapters
                .get().projectHandler().removeAll();
    }

    static Observable<Project> findAll() {
        return Adapters
                .get().projectHandler().findAll();
    }

    long latestUpdate();

    String name();


    Project setName(String artifactId);

    Project setSnapshot(String version);

    Project setRelease(String version);

    Project setTables(List<String> tables);

    Project setJavaDeps(List<String> javaFilters);

    List<String> javaDeps( );

    Project setChangeLog(String changeLog);

    Project setApis(final List<String> apis);

    Project setLatestUpdate(Long update);

    Single<Boolean> save();

    String id();

    String release();

    String snapshot();

    List<String> tables();

    List<String> apis();

    String changelog();
}
