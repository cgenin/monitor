package net.christophe.genin.domain.server.db.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.UpdateResult;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.ConfigurationDto;
import net.christophe.genin.domain.server.db.Schemas;
import rx.Observable;
import rx.Single;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MysqlCommand implements Commands {
    private static final Logger logger = LoggerFactory.getLogger(Mysqls.VertxMysql.class);



    public Single<Integer> exportEvents(Stream<JsonObject> jsons) {
        Mysqls mysqls = Mysqls.Instance.get();
        return mysqls.connection()
                .flatMap(conn -> conn.rxSetAutoCommit(false)
                        .flatMap(v -> {
                            Set<Observable<UpdateResult>> collect = jsons
                                    .map(json -> {
                                        Integer state = json.getInteger("state");
                                        Boolean archive = json.getBoolean("ARCHIVE");
                                        return new JsonArray().add(state).add(json.encode()).add(archive);
                                    })
                                    .map(params -> conn.rxUpdateWithParams("INSERT EVENTS (state, document, ARCHIVE) VALUES (?,?,?)", params).toObservable())
                                    .collect(Collectors.toSet());
                            return Observable.concat(Observable.from(collect))
                                    .map(updateResult -> {
                                        int updated = updateResult.getUpdated();
                                        if (updated == 0) {
                                            throw new IllegalStateException("Impossible to insert one data");
                                        }
                                        return updated;
                                    })
                                    .reduce(0, (acc, a) -> acc + a)
                                    .toSingle();
                        })
                        .doOnError(err -> {
                            conn.rxRollback()
                                    .flatMap(v2 -> conn.rxClose());
                        })
                        .flatMap(rs -> conn.rxCommit()
                                .flatMap(v2 -> conn.rxClose())
                                .map(v3 -> rs)
                        ));
    }
}
