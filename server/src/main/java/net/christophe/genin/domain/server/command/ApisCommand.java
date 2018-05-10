package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.domain.server.Console;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.model.Api;
import net.christophe.genin.domain.server.model.Project;
import net.christophe.genin.domain.server.model.Raw;
import rx.Observable;
import rx.Single;

import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ApisCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ApisCommand.class);

    static Observable<JsonObject> servicesToJson(JsonArray services) {
        Function<JsonObject, Stream<? extends JsonObject>> convert2Json = obj -> {
            String className = obj.getString(Schemas.Raw.Apis.Services.name.name(), "");
            JsonArray methods = obj.getJsonArray(Schemas.Raw.Apis.Services.methods.name(), new JsonArray());
            return Jsons.builder(methods)
                    .toStream()
                    .map(o -> o.put("className", className));
        };
        return Observable.from(
                Jsons.builder(services).toStream().flatMap(convert2Json).collect(Collectors.toList())
        );
    }

    @Override
    public void start() {
        new Treatments.Periodic(this, logger).run(this::periodic);
        logger.info("started");
    }

    private synchronized boolean periodic() {
        Raw.findByStateFirst(Treatments.URL)
                .flatMap(doc -> {
                    final JsonObject json = doc.json();
                    final JsonObject apis = json.getJsonObject(Schemas.Raw.apis.name(), new JsonObject());
                    String artifactId = json.getString(Schemas.Raw.Apis.artifactId.name(), "");
                    String version = json.getString(Schemas.Raw.Apis.version.name(), "");
                    final long update = json.getLong(Schemas.Raw.update.name());
                    JsonArray services = apis.getJsonArray(Schemas.Raw.Apis.services.name(), new JsonArray());


                    return Project.findByName(artifactId)
                            .toObservable()
                            .flatMap(project -> {
                                String idProject = project.id();
                                return Api.deleteByIdProject(idProject)
                                        .doOnNext(nb -> {
                                            logger.debug("DELETE for '" + artifactId + "' = " + nb);
                                        })
                                        .flatMap(nb -> servicesToJson(services))
                                        .flatMap(methodJson -> {
                                            String groupId = apis.getString(Schemas.Raw.Apis.groupId.name(), "");
                                            final String method = methodJson.getString("method", "");
                                            final String path = methodJson.getString("path", "");
                                            String newId = UUID.randomUUID().toString();
                                            Api api = Api.newInstance(method, path, idProject)
                                                    .setId(newId)
                                                    .setArtifactId(artifactId)
                                                    .setGroupId(groupId)
                                                    .setSince(version)
                                                    .setLatestUpdate(update)
                                                    .setName(methodJson.getString("name"))
                                                    .setReturns(methodJson.getString("returns"))
                                                    .setParams(methodJson.getJsonArray("params").encode())
                                                    .setComment(methodJson.getString("comment"))
                                                    .setClassName(methodJson.getString("className"));
                                            return Single.just(api)
                                                    .flatMap(Api::create)
                                                    .map(updateResult ->
                                                            "Api '" + path + "' for artifact '" + artifactId + "' updated : " + updateResult
                                                    ).toObservable();
                                        });
                            })
                            .doOnCompleted(() -> doc.updateState(Treatments.DEPENDENCIES)
                                .subscribe(bool -> logger.info("Api for "+artifactId+" was updated to next :"+bool)));

                }).subscribe(str -> {
                    logger.info(str);
                    vertx.eventBus().send(Console.INFO, str);
                },
                err -> {
                    logger.error("Error in projects batch", err);
                });
        return true;
    }


}
