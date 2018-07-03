package net.christophe.genin.monitor.domain.server.db.mysql;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.flywaydb.core.Flyway;
import rx.Single;
import rx.schedulers.Schedulers;

public class FlywayVerticle extends AbstractVerticle {


    public static final String UPGRADE = FlywayVerticle.class.getName() + ".upgrade";
    private static final Logger logger = LoggerFactory.getLogger(FlywayVerticle.class);


    @Override
    public void start() {

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

                // Start the migration
                return flyway.migrate();
            }).subscribeOn(Schedulers.io())
                    .map(nb -> new JsonObject().put("nbMigration", nb))
                    .subscribe(
                            o ->{
                                msg.reply(o);
                            },
                            err -> {
                                logger.error("error in migrate", err);
                                msg.fail(500, "error in migrate");
                            });

        });

    }
}
