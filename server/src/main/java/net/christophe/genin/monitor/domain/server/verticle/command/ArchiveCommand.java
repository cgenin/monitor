package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.model.Raw;
import net.christophe.genin.monitor.domain.server.model.StoredServiceEvent;
import rx.Single;

public class ArchiveCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ArchiveCommand.class);

    public static final String ARCHIVE = ImportCommand.class.getName() + ".archive";
    private static final String EXPORTED = "numberOfExported";
    private static final String DELETED = "numberOfDeleted";

    @Override
    public void start() {

        vertx.eventBus().<JsonObject>consumer(ARCHIVE, (msg) -> {
            IRaw iRaw = Adapters.get().rawHandler();
            IStoredServiceEvent iStoredServiceEvent = Adapters.get().storedServiceEventHandler();

            RawDomain rawDomain = new RawDomain(iRaw);
            StoredServiceEventDomain storedServiceEventDomain = new StoredServiceEventDomain(iStoredServiceEvent);
            rawDomain
                    .findAllByState(Treatments.END)
                    .map(raw -> storedServiceEventDomain.newInstance(raw.json(), raw.update())
                            .setArchive(true)
                            .setOriginalId(raw.id()))
                    .flatMap(sse -> sse.create()
                            .flatMap(bool -> {
                                if (bool) {
                                    return rawDomain.removeById(sse.originalId())
                                            .map(deleted -> {
                                                if (deleted) {
                                                    return new JsonObject()
                                                            .put(EXPORTED, 1)
                                                            .put(DELETED, 1);
                                                }
                                                return new JsonObject()
                                                        .put(EXPORTED, 1)
                                                        .put(DELETED, 0);
                                            });
                                }
                                return Single.just(new JsonObject()
                                        .put(EXPORTED, 0)
                                        .put(DELETED, 0));
                            })
                            .toObservable()
                    )
                    .reduce(new JsonObject()
                                    .put(EXPORTED, 0)
                                    .put(DELETED, 0),
                            (acc, obj) -> new JsonObject()
                                    .put(EXPORTED, acc.getInteger(EXPORTED) + obj.getInteger(EXPORTED))
                                    .put(DELETED, acc.getInteger(DELETED) + obj.getInteger(DELETED)))
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("Error in exporting to MysqlPort", err);
                                msg.fail(500, "Error in exporting to MysqlPort");
                            });
        });
    }
}
