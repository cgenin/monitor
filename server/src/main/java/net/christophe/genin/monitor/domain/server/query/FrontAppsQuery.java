package net.christophe.genin.monitor.domain.server.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.monitor.domain.server.command.util.Raws;
import net.christophe.genin.monitor.domain.server.model.Configuration;
import net.christophe.genin.monitor.domain.server.model.FrontApps;

import java.util.HashSet;
import java.util.List;

public class FrontAppsQuery extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(FrontAppsQuery.class);

    public static final String FIND_ALL = FrontAppsQuery.class.getName() + ".find.all";
    public static final String FIND_BY_GROUP = FrontAppsQuery.class.getName() + ".find.group.by";


    @Override
    public void start() {

        vertx.eventBus().consumer(FIND_ALL, msg -> {
            FrontApps.findAll()
                    .map(fa -> new JsonObject()
                            .put("id", fa.id())
                            .put("groupId", fa.groupId())
                            .put("artifactId", fa.artifactId())
                            .put("version", fa.version())
                            .put("lastUpdate", fa.lastUpdate())
                            .put("packagesJson", fa.packagesJson()))

                    .reduce(new JsonArray(), JsonArray::add)
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error", err);
                                msg.fail(500, "error");
                            });
        });

        vertx.eventBus().consumer(FIND_BY_GROUP, msg ->
                Configuration.load()
                        .toObservable()
                        .map(Configuration::npmFilters)
                        .flatMap(npmFilters ->
                                FrontApps.findAll()
                                        .groupBy(fa -> fa.groupId() + "." + fa.artifactId())
                                        .flatMap(groups -> groups.collect(
                                                () -> new JsonObject()
                                                        .put("lastUpdateSnapshot", 0L)
                                                        .put("lastUpdateRelease", 0L)
                                                        .put("packageJson", new JsonObject())
                                                        .put("lastUpdate", 0L)
                                                ,
                                                (acc, fa) -> {
                                                    acc.put("groupId", fa.groupId())
                                                            .put("artifactId", fa.artifactId());
                                                    if (Raws.isSnapshot(fa.version())) {
                                                        if (acc.getLong("lastUpdateSnapshot") < fa.lastUpdate()) {
                                                            acc.put("snapshotVersion", fa.version())
                                                                    .put("lastUpdateSnapshot", fa.lastUpdate());
                                                        }
                                                    } else if (acc.getLong("lastUpdateRelease") < fa.lastUpdate()) {
                                                        acc.put("releaseVersion", fa.version())
                                                                .put("lastUpdateRelease", fa.lastUpdate());
                                                    }

                                                    if (acc.getLong("lastUpdate") < fa.lastUpdate()) {
                                                        JsonObject packagesJson = fa.packagesJson();
                                                        acc.put("packageJson", packagesJson)
                                                                .put("lastUpdate", fa.lastUpdate());
                                                        acc.put("servicesClient", servicesClient(packagesJson, npmFilters));
                                                    }
                                                }
                                        ))
                        )
                        .reduce(new JsonArray(), JsonArray::add)
                        .subscribe(
                                msg::reply,
                                err -> {
                                    logger.error("error", err);
                                    msg.fail(500, "error");
                                }));

        logger.info("started");
    }

    private JsonArray servicesClient(JsonObject packagesJson, List<String> npmFilters) {
        HashSet<String> npms = new HashSet<>(npmFilters);
        JsonObject dependencies = packagesJson.getJsonObject("dependencies").copy();
        return dependencies.fieldNames().stream()
                .filter(key -> npms.stream().anyMatch(key::contains))
                .distinct()
                .map(key -> new JsonObject().put("key", key).put("value", dependencies.getString(key)))
                .collect(Jsons.Collectors.toJsonArray());
    }
}
