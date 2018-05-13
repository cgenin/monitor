package net.christophe.genin.monitor.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.model.*;
import rx.Observable;
import rx.schedulers.Schedulers;


public class BackupQuery extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(BackupQuery.class);

    public static final String DUMP = BackupQuery.class.getName() + ".dump";

    @Override
    public void start() {
        vertx.eventBus().consumer(DUMP,
                msg -> {
                    Observable<JsonObject> api = api();
                    Observable<JsonObject> projects = projects();
                    Observable<JsonObject> tables = tables();
                    Observable<JsonObject> versions = versions();
                    Observable<JsonObject> dependencies = dependencies();
                    Observable<JsonObject> raws = raws();
                    Observable.concat(api, projects, tables, versions, dependencies, raws)
                            .reduce(new JsonObject(), JsonObject::mergeIn)
                            .subscribeOn(Schedulers.computation())
                            .subscribe(
                                    msg::reply,
                                    err -> {
                                        logger.error("error in " + DUMP, err);
                                        msg.fail(500, "Error in query");
                                    }
                            );
                }
        );
        logger.info("started");
    }

    private Observable<JsonObject> raws() {
        return Raw.findAll()
                .map(raw -> new JsonObject()
                        .put("artifactId", raw.artifactId())
                        .put("update", raw.update())
                        .put("json", raw.json())
                        .put("state", raw.state().getState())
                )
                .reduce(new JsonArray(), JsonArray::add)
                .map(arr -> new JsonObject().put("raws", arr));
    }

    private Observable<JsonObject> dependencies() {
        return Dependency.findAll()
                .map(dependency -> new JsonObject()
                        .put("RESOURCE", dependency.resource())
                        .put("USED_BY", dependency.usedBy()))
                .reduce(new JsonArray(), JsonArray::add)
                .map(arr -> new JsonObject().put("dependencies", arr));
    }

    private Observable<JsonObject> versions() {
        return Version.findAll()
                .map(version -> new JsonObject()
                                .put("ID", version.id())
                                .put("NAME", version.name())
                                .put("IDPROJECT", version.idProject())
                        // TODO
                )
                .reduce(new JsonArray(), JsonArray::add)
                .map(arr -> new JsonObject().put("versions", arr));
    }


    private Observable<JsonObject> tables() {
        return Table.findAll()
                .map(table -> new JsonObject()
                        .put("ID", table.id())
                        .put("NAME", table.tableName())
                        .put("SERVICE", table.service())
                        .put("latestUpdate", table.lastUpdated()))
                .reduce(new JsonArray(), JsonArray::add)
                .map(arr -> new JsonObject().put("tables", arr));
    }

    public Observable<JsonObject> projects() {
        return Project.findAll()
                .map(project -> new JsonObject()
                                .put("ID", project.id())
                                .put("NAME", project.name())
                        // TODO
                )
                .reduce(new JsonArray(), JsonArray::add)
                .map(arr -> new JsonObject().put("projects", arr));
    }

    public Observable<JsonObject> api() {
        return Api.findAll()
                .map(api -> new JsonObject()
                                .put("ID", api.id())
                                .put("METHOD", api.method())
                                .put("FULLURL", api.path())
                                .put("IDPROJECT", api.idProject())
                        // TODO
                )
                .reduce(new JsonArray(), JsonArray::add)
                .map(arr -> new JsonObject().put("apis", arr));
    }
}
