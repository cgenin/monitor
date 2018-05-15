package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.monitor.domain.server.db.Schemas;
import net.christophe.genin.monitor.domain.server.model.Raw;
import net.christophe.genin.monitor.domain.server.model.Table;
import rx.Observable;
import rx.functions.Func1;

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

    private synchronized Observable<String> periodic() {
       return Raw.findByStateFirst(Treatments.TABLES)
                .flatMap(run());
    }

    Func1<Raw, Observable<? extends String>> run() {
        return raw -> {
            final JsonObject json = raw.json();
            final List<String> tables = Jsons.builder(json.getJsonArray(Schemas.Raw.Tables.collection())).toStream()
                    .map(js -> js.getString(Schemas.Raw.Tables.table.name(), ""))
                    .collect(Collectors.toList());
            final String artifactId = raw.artifactId();

            final long update = raw.update();

            Observable<String> creationTable = Table.findByService(artifactId)
                    .flatMap(rs -> {
                        if (Objects.isNull(rs)) {
                            return Observable.from(tables);
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
                    .doOnCompleted(() -> raw.updateState(Treatments.VERSION).subscribe(
                            bool -> logger.info("Tables (" + tables + ") in project " + artifactId + " wad updated to next :" + bool)
                    ));

        };
    }

}
