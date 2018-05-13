package net.christophe.genin.monitor.domain.server.adapter;

import io.vertx.rxjava.core.AbstractVerticle;
import net.christophe.genin.monitor.domain.server.adapter.mysql.*;
import net.christophe.genin.monitor.domain.server.adapter.nitrite.*;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.model.port.*;

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

    public DependencyPort dependencyHandler() {
        switch (type.get()) {
            case MYSQL:
                return new MysqlDependency.MysqlDependencyPort(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteDependency.NitriteDependencyPort(NitriteDbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public ProjectPort projectHandler() {
        switch (type.get()) {
            case MYSQL:
                return new MysqlProject.MysqlProjectPort(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteProject.NitriteProjectPort(NitriteDbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public TablePort tableHandler() {
        switch (type.get()) {
            case MYSQL:
                return new MysqlTable.MysqlTablePort(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteTable.NitriteTablePort(NitriteDbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public VersionPort versionHandler() {
        switch (type.get()) {
            case MYSQL:
                return new MysqlVersion.MysqlVersionHandler(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteVersion.NitriteVersionHandler(NitriteDbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public ApiPort apiHandler() {
        switch (type.get()) {
            case MYSQL:
                return new MysqlApi.MysqlApiPort(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteApi.NitriteApiPort(NitriteDbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public enum Type {
        NITRITE, MYSQL
    }
}
