package net.christophe.genin.monitor.domain.server.adapter;

import io.vertx.rxjava.core.AbstractVerticle;
import net.christophe.genin.monitor.domain.server.adapter.mysql.*;
import net.christophe.genin.monitor.domain.server.adapter.nitrite.*;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.model.port.*;

/**
 * Select And create the current Port for the current implementation.
 */
public class Adapters extends AbstractVerticle {

    public static final String CHANGE_DATABASE = Adapters.class.getName() + ".change.database";

    public static Singleton type = new Singleton();


    public static Adapters get() {
        return new Adapters();
    }

    @Override
    public void start() {
        vertx.eventBus().<String>consumer(CHANGE_DATABASE, msg -> {
            String t = msg.body();
            Type typeLocal = Type.valueOf(t);
            type.setType(typeLocal);
        });
    }

    public DependencyPort dependencyHandler() {
        switch (type.getType()) {
            case MYSQL:
                return new MysqlDependency.MysqlDependencyPort(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteDependency.NitriteDependencyPort(NitriteDbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public RawPort rawHandler() {
        switch (type.getType()) {
            case MYSQL:
                return new MysqlRaw.MysqlRawPort(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteRaw.NitriteRawPort(NitriteDbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }


    public ProjectPort projectHandler() {
        switch (type.getType()) {
            case MYSQL:
                return new MysqlProject.MysqlProjectPort(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteProject.NitriteProjectPort(NitriteDbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public TablePort tableHandler() {
        switch (type.getType()) {
            case MYSQL:
                return new MysqlTable.MysqlTablePort(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteTable.NitriteTablePort(NitriteDbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public VersionPort versionHandler() {
        switch (type.getType()) {
            case MYSQL:
                return new MysqlVersion.MysqlVersionHandler(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteVersion.NitriteVersionPort(NitriteDbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public ApiPort apiHandler() {
        switch (type.getType()) {
            case MYSQL:
                return new MysqlApi.MysqlApiPort(Mysqls.Instance.get());
            case NITRITE:
                return new NitriteApi.NitriteApiPort(NitriteDbs.instance);
            default:
                throw new IllegalStateException("No type found " + type);
        }
    }

    public StoredServiceEventPort storedServiceEventHandler() {
        switch (type.getType()) {
            case MYSQL:
                return new MysqlStoredServiceEvent.MysqlStoredServiceEventPort(Mysqls.Instance.get());
            case NITRITE:
            default:
                throw new IllegalStateException("Only in mysql mode " + type);
        }
    }

    public enum Type {
        NITRITE, MYSQL
    }

    public static class Singleton {

        private Type type = Type.NITRITE;

        public synchronized Type getType() {
            return type;
        }

        public synchronized void setType(Type type) {
            this.type = type;
        }
    }
}
