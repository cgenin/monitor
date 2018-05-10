package net.christophe.genin.domain.server.adapter;

import io.vertx.rxjava.core.AbstractVerticle;
import net.christophe.genin.domain.server.adapter.mysql.*;
import net.christophe.genin.domain.server.adapter.nitrite.*;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.model.handler.*;

/**
 * Select And create the current handler for the good implementation.
 */
public class Adapters extends AbstractVerticle {

    public static final String CHANGE_DATABASE = Adapters.class.getName() + ".change.database";

    private static ThreadLocal<Type> type = ThreadLocal.withInitial(() -> Type.NITRITE);


    public static Adapters get() {
        return new Adapters();
    }

    @Override
    public void start() {
        vertx.eventBus().<String>consumer(CHANGE_DATABASE, msg -> {
            String t = msg.body();
            Type typeLocal = Type.valueOf(t);
            type.set(typeLocal);
        });
    }

    public DependencyHandler dependencyHandler() {
        switch (type.get()) {
            case MYSQL:
                return new MysqlDependency.MysqlDependencyHandler(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteDependency.NitriteDependencyHandler(Dbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public ProjectHandler projectHandler() {
        switch (type.get()) {
            case MYSQL:
                return new MysqlProject.MysqlProjectHandler(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteProject.NitriteProjectHandler(Dbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public TableHandler tableHandler() {
        switch (type.get()) {
            case MYSQL:
                return new MysqlTable.MysqlTableHandler(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteTable.NitriteTableHandler(Dbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public VersionHandler versionHandler() {
        switch (type.get()) {
            case MYSQL:
                return new MysqlVersion.MysqlVersionHandler(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteVersion.NitriteVersionHandler(Dbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public ApiHandler apiHandler() {
        switch (type.get()) {
            case MYSQL:
                return new MysqlApi.MysqlApiHandler(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteApi.NitriteApiHandler(Dbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public enum Type {
        NITRITE, MYSQL
    }
}
