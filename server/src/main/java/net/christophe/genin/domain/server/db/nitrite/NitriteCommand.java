package net.christophe.genin.domain.server.db.nitrite;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.ConfigurationDto;
import net.christophe.genin.domain.server.db.Schemas;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Observable;
import rx.Single;

import java.util.*;

import static org.dizitart.no2.filters.Filters.and;
import static org.dizitart.no2.filters.Filters.eq;

public class NitriteCommand implements Commands {

    private static final Logger logger = LoggerFactory.getLogger(NitriteCommand.class);

    public Single<String> reset() {
        return Single.fromCallable(() -> {
            Dbs.instance.nitrite().listCollectionNames()
                    .parallelStream()
                    .filter(s -> s.startsWith(Schemas.Version.PREFIX))
                    .forEach(name -> Dbs.instance.nitrite().getCollection(name).drop());
            logger.info("version collections deleted");
            Dbs.instance.getCollection(Schemas.Tables.collection()).drop();
            logger.info("tables collections deleted");
            Dbs.instance.getCollection(Schemas.Projects.collection()).drop();
            logger.info("project collections deleted");
            return "Datas cleared";
        });
    }

}
