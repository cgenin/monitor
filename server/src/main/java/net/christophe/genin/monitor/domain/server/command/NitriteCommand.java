package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import net.christophe.genin.monitor.domain.server.adapter.nitrite.*;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import rx.Observable;
import rx.functions.Func1;

public class NitriteCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(NitriteCommand.class);
    public static final String CLEAR_CALCULATE_DATA = RawCommand.class.getName() + ".clear.calculate.data";

    private Func1<Integer, JsonObject> adapt(String nameCollection) {
        return size -> new JsonObject().put("collectionName", nameCollection).put("size", size);
    }

    @Override
    public void start() {
        vertx.eventBus().consumer(CLEAR_CALCULATE_DATA, rc -> {
            Observable<JsonObject> projects = new NitriteProject.NitriteProjectPort(NitriteDbs.instance)
                    .removeAll()
                    .map(adapt("project"))
                    .toObservable();
            Observable<JsonObject> apis = new NitriteApi.NitriteApiPort(NitriteDbs.instance)
                    .removeAll()
                    .map(adapt("api"))
                    .toObservable();
            Observable<JsonObject> tables = new NitriteTable.NitriteTablePort(NitriteDbs.instance)
                    .removeAll()
                    .map(adapt("table"))
                    .toObservable();
            Observable<JsonObject> versions = new NitriteVersion.NitriteVersionPort(NitriteDbs.instance)
                    .removeAll()
                    .map(adapt("version"))
                    .toObservable();
            Observable<JsonObject> dependencies = new NitriteDependency.NitriteDependencyPort(NitriteDbs.instance)
                    .removeAll()
                    .map(adapt("dependency"))
                    .toObservable();
            Observable.concat(projects, apis, tables, versions, dependencies)
                    .reduce(new JsonArray(), JsonArray::add)
                    .subscribe(
                            rc::reply,
                            err -> {
                                logger.error("Error in clearing in calculating data", err);
                                rc.fail(500, "Error in clearing in calculating data");
                            });

        });

    }
}
