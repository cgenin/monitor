package net.christophe.genin.domain.server.db.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.command.Treatments;
import rx.Single;

public class Events {

    public static Single<Long> insert(JsonObject body) {
        return Mysqls.Instance.get().execute("INSERT INTO events " +
                "( state, document) VALUES " +
                "( ?, ?)", new JsonArray().add(Treatments.PROJECTS.getState()).add(body.encode()))
                .map(updateResult -> {
                    JsonArray keys = updateResult.getKeys();
                    return keys.getLong(0);
                });
    }
}
