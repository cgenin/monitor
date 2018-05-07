package net.christophe.genin.domain.server.model;

import net.christophe.genin.domain.server.adapter.mysql.MysqlDependency.MysqlDependencyHandler;
import net.christophe.genin.domain.server.adapter.nitrite.NitriteDependency.NitriteDependencyHandler;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import rx.Single;

public abstract class Dependency {

    public static Single<Integer> removeByUsedBy(String usedBY) {
        return (Mysqls.Instance.get().active()) ?
                new MysqlDependencyHandler(Mysqls.Instance.get()).removeByUsedBy(usedBY) :
                new NitriteDependencyHandler(Dbs.instance).removeByUsedBy(usedBY);
    }

    public static Single<Boolean> create(String resource, String usedBY) {
        return (Mysqls.Instance.get().active()) ?
                new MysqlDependencyHandler(Mysqls.Instance.get()).create(resource, usedBY):
                new NitriteDependencyHandler(Dbs.instance).create(resource, usedBY);
    }

    public static Single<Integer> removeAll() {
        return (Mysqls.Instance.get().active()) ?
                new MysqlDependencyHandler(Mysqls.Instance.get()).removeAll():
                new NitriteDependencyHandler(Dbs.instance).removeAll();
    }
}
