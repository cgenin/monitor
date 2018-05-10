package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.domain.server.Console;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.model.Raw;
import net.christophe.genin.domain.server.model.Table;
import rx.Observable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class TablesCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(TablesCommand.class);

    @Override
    public void start() {
        new Treatments.Periodic(this, logger).run(this::periodic);
    }


    private synchronized boolean periodic() {

        Raw.findByStateFirst(Treatments.TABLES)
                .flatMap(doc -> {
                    final JsonObject json = doc.json();
                    final List<String> tables = Jsons.builder(json.getJsonArray(Schemas.Raw.Tables.collection())).toStream()
                            .map(js -> js.getString(Schemas.Raw.Tables.table.name(), ""))
                            .collect(Collectors.toList());
                    final String artifactId = doc.artifactId();

                    final long update = doc.update();

                    rx.Observable<String> creationTable = Table.findByService(artifactId)
                            .flatMap(rs -> {
                                if (Objects.isNull(rs)) {
                                    return rx.Observable.from(tables);
                                }
                                HashSet<String> toCreate = new HashSet<>(tables);
                                toCreate.removeAll(rs);
                                return Observable.from(toCreate);
                            })
                            .flatMap(tableName -> {
                                Table newTable = Table.newInstance();
                                newTable.setTableName(tableName)
                                        .setService(artifactId)
                                        .setLastUpdated(update);
                                return newTable.create()
                                        .map(updateResult -> "Table '" + tableName + "' for '" + artifactId + "' creation " + updateResult)
                                        .toObservable();
                            });
                    Observable<String> deletion = Table.findByService(artifactId)
                            .flatMap(rs -> {
                                if (Objects.isNull(rs)) {
                                    return Observable.empty();
                                }

                                rs.removeAll(tables);
                                return Observable.from(rs);
                            })
                            .flatMap(tableName -> Table.remove(tableName, artifactId)
                                    .map(updateResult -> "Table '" + tableName + "' for '" + artifactId + "'  deleted :" + updateResult)
                                    .toObservable()
                            );
                    return Observable.concat(deletion, creationTable)
                            .doOnCompleted(() -> doc.updateState(Treatments.VERSION).subscribe(
                                    bool -> logger.info("Tables (" + tables + ") in project " + artifactId + " wad updated to next :" + bool)
                            ));

                }).subscribe(
                str -> {
                    logger.info(str);
                    vertx.eventBus().send(Console.INFO, str);
                },
                err -> {
                    logger.error("error in tables for ", err);
                }
        );

        return true;
    }

}
