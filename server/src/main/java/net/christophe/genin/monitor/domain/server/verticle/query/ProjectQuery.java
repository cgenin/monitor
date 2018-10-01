package net.christophe.genin.monitor.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.model.Project;
import net.christophe.genin.monitor.domain.server.model.Version;

import java.util.Optional;

/**
 * Read operation for domain service.
 */
public class ProjectQuery extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ProjectQuery.class);

    public static final String LIST = ProjectQuery.class.getName() + ".list";
    public static final String GET = ProjectQuery.class.getName() + ".get";
    public static final String ID = "id";


    @Override
    public void start() {
        vertx.eventBus().consumer(LIST,
                msg -> {
                    Project.findAll()
                            .map(project -> {
                                final JsonObject obj = new JsonObject();
                                Optional.ofNullable(project.release())
                                        .ifPresent(s -> obj.put(Schemas.Projects.release.name(), s));
                                Optional.ofNullable(project.snapshot())
                                        .ifPresent((s) -> obj.put(Schemas.Projects.snapshot.name(), s));
                                return obj
                                        .put(Schemas.Projects.id.name(), project.id())
                                        .put(Schemas.Projects.name.name(), project.name())
                                        .put(Schemas.Projects.latestUpdate.name(), project.latestUpdate())
                                        .put(Schemas.Projects.tables.name(), new JsonArray(project.tables()))
                                        .put(Schemas.Projects.apis.name(), new JsonArray(project.apis()))
                                        .put(Schemas.Projects.changelog.name(), project.changelog())
                                        .put(Schemas.Projects.javaDeps.name(), new JsonArray(project.javaDeps()));
                            })
                            .reduce(new JsonArray(), JsonArray::add)
                            .subscribe(
                                    msg::reply,
                                    err -> {
                                        logger.error("error in " + LIST, err);
                                        msg.fail(500, "Error in query");
                                    }
                            );
                });
        vertx.eventBus().<JsonObject>consumer(GET, msg -> {
            String id = msg.body().getString(ID, "");
            IVersion versionDao = Adapters.get().versionHandler();
            new VersionDomain(versionDao)
                    .findByProject(id)
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error in " + GET, err);
                                msg.fail(500, "Error in query");
                            }
                    );
        });

        logger.info("started");
    }
}
