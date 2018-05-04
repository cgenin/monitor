package net.christophe.genin.domain.server.model;

import net.christophe.genin.domain.server.adapter.mysql.MysqlProject;
import net.christophe.genin.domain.server.adapter.nitrite.NitriteProject;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import rx.Single;

import java.util.List;

public interface Project {

    static Single<Project> readByName(String artifactId) {
        return (!Mysqls.Instance.get().active()) ?
                MysqlProject.readByName(artifactId) :
                NitriteProject.readByName(artifactId);
    }

    long latestUpdate();

    String name();


    Project setName(String artifactId);

    Project setSnapshot(String version);

    Project setRelease(String version);

    Project setTables(List<String> tables);

    Project setJavaDeps(List<String> javaFilters);

    Project setChangeLog(String changeLog);

    Project setApis(final List<String> apis);

    Project setLatestUpdate(Long update);

    Single<Boolean> save();
}
