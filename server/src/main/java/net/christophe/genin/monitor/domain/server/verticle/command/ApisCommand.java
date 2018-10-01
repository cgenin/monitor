package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.monitor.domain.server.Console;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.model.Api;
import net.christophe.genin.monitor.domain.server.model.Project;
import net.christophe.genin.monitor.domain.server.model.Raw;
import rx.Observable;


public class ApisCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ApisCommand.class);



    @Override
    public void start() {
        new Periodic(this, logger).run(this::periodic);
        logger.info("started");
    }

    private synchronized Observable<String> periodic() {
        IRaw iRaw = Adapters.get().rawHandler();
        IApi iApi = Adapters.get().apiHandler();
        return new RawDomain(iRaw)
                .findByStateFirst(Treatments.URL)
                .flatMap(doc -> {
                    final JsonObject json = doc.json();
                    final JsonObject apis = json.getJsonObject(Schemas.Raw.apis.name(), new JsonObject());
                    String artifactId = json.getString(Schemas.Raw.Apis.artifactId.name(), "");
                    String version = json.getString(Schemas.Raw.Apis.version.name(), "");
                    final long update = doc.update();
                    JsonArray services = apis.getJsonArray(Schemas.Raw.Apis.services.name(), new JsonArray());


                    return Project.findByName(artifactId)
                            .toObservable()
                            .map(Project::id)
                            .flatMap(idProject -> new ApiDomain(iApi).saveByProject(idProject, apis, artifactId, version, update))
                            .doOnCompleted(() -> doc.updateState(Treatments.DEPENDENCIES)
                                    .subscribe(bool -> logger.info("Api for " + artifactId + " was updated to next :" + bool)));

                });
    }

}
