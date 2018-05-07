package net.christophe.genin.domain.server.model;

import net.christophe.genin.domain.server.adapter.mysql.MysqlProject;
import net.christophe.genin.domain.server.adapter.nitrite.NitriteProject;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import rx.Single;

import java.util.List;

public interface Project {

    static Single<Project> findByName(String artifactId) {
        return (Mysqls.Instance.get().active()) ?
                new MysqlProject.MysqlProjectHandler(Mysqls.Instance.get()).readByName(artifactId) :
                new NitriteProject.NitriteProjectHandler(Dbs.instance).readByName(artifactId);
    }

    static  Single<Integer> removeAll(){
        return (Mysqls.Instance.get().active()) ?
                new MysqlProject.MysqlProjectHandler(Mysqls.Instance.get()).removeAll() :
                new NitriteProject.NitriteProjectHandler(Dbs.instance).removeAll();
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
}
