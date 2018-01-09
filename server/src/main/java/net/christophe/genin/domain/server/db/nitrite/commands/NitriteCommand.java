package net.christophe.genin.domain.server.db.nitrite.commands;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.db.Commands;
import rx.Observable;
import rx.Single;

public class NitriteCommand implements Commands {

    @Override
    public Observable<String> projects(JsonObject json, String artifactId) {
        if (new NitriteProject(json, artifactId).insert()) {
            return Observable.just("Projects '" + artifactId + "' updated");
        }
        return Observable.just("Projects '" + artifactId + "' not updated");
    }

    @Override
    public boolean versions(JsonObject json, String artifactId) {
        return new NitriteVersion(json, artifactId).insert();
    }
}
