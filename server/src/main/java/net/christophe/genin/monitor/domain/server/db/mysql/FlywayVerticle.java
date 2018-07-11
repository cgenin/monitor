package net.christophe.genin.monitor.domain.server.db.mysql;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationState;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.Date;
import java.util.Optional;

public class FlywayVerticle extends AbstractVerticle {


    public static final String UPGRADE = FlywayVerticle.class.getName() + ".upgrade";
    public static final String GET_STATE = FlywayVerticle.class.getName() + ".get.state";
    private static final Logger logger = LoggerFactory.getLogger(FlywayVerticle.class);


    @Override
    public void start() {


        vertx.eventBus().<JsonObject>consumer(GET_STATE, msg -> {
            JsonObject body = msg.body();
            String url = body.getString("url", "");
            String user = body.getString("username");
            String password = body.getString("password");
            Observable.fromCallable(() -> {
                // Create the Flyway instance
                Flyway flyway = new Flyway();
                // Point it to the database
                flyway.setDataSource(url, user, password);
                // Start the migration
                return flyway.info();
            })
                    .subscribeOn(Schedulers.io())
                    .flatMap(migrationInfoService -> Observable.from(migrationInfoService.all()))
                    .map(migrationInfo -> {
                        MigrationState migrationState = migrationInfo.getState();
                        JsonObject state = new JsonObject()
                                .put("name", migrationState.getDisplayName())
                                .put("applied", migrationState.isApplied())
                                .put("failed", migrationState.isFailed())
                                .put("resolved", migrationState.isResolved());

                        JsonObject result = new JsonObject().put("description", migrationInfo.getDescription())
                                .put("installedrank", migrationInfo.getInstalledRank())
                                .put("script", migrationInfo.getScript())
                                .put("executiontime", migrationInfo.getExecutionTime())
                                .put("state", state);
                        return Optional.ofNullable(migrationInfo.getInstalledOn())
                                .map(Date::getTime)
                                .map(dt -> result.put("installedon", dt))
                                .orElse(result);
                    })
                    .reduce(new JsonArray(), JsonArray::add)
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error", err);
                                msg.fail(500, "error");
                            });

        });

        vertx.eventBus().<JsonObject>consumer(UPGRADE, msg -> {
            JsonObject body = msg.body();
            String url = body.getString("url", "");
            String user = body.getString("username");
            String password = body.getString("password");

            Single.fromCallable(() -> {
                // Create the Flyway instance
                Flyway flyway = new Flyway();

                // Point it to the database
                flyway.setDataSource(url, user, password);
                flyway.setBaselineOnMigrate(true);
                // Start the migration
                return flyway.migrate();
            }).subscribeOn(Schedulers.io())
                    .map(nb -> new JsonObject().put("nbMigration", nb))
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error in migrate", err);
                                msg.fail(500, "error in migrate");
                            });

        });

    }
}
