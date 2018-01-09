package net.christophe.genin.domain.server.db;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.json.Jsons;
import rx.Observable;
import rx.Single;

import java.util.List;
import java.util.stream.Collectors;

public interface Commands {

    Observable<String> projects(JsonObject json, String artifactId);

    boolean versions(JsonObject json, String artifactId);

    class Projects {
       public static boolean isSnapshot(String version) {
            return version.contains("SNAPSHOT");
        }

        public static List<String> extractJavaDeps(JsonObject json) {


            return Jsons.builder(json.getJsonArray(Schemas.Raw.Dependencies.collection())).toStream()
                    .map(js -> js.getString(Schemas.Raw.Dependencies.artifactId.name(), ""))
                    .distinct()
                    .collect(Collectors.toList());
        }

        public static List<String> extractUrls(JsonObject json) {
            JsonObject apis = json.getJsonObject(Schemas.Raw.apis.name(), new JsonObject());
            JsonArray services = apis.getJsonArray("services", new JsonArray());


            return Jsons.builder(services).toStream()
                    .map(js -> js.getJsonArray("methods", new JsonArray()))
                    .flatMap(arr -> Jsons.builder(arr).toStream())
                    .map(js -> {
                        final String method = js.getString("method", "");
                        final String path = js.getString("path", "");
                        return method + " - " + path;
                    })
                    .distinct()
                    .collect(Collectors.toList());
        }

        public static List<String> extractTables(JsonObject json) {
            return Jsons.builder(json.getJsonArray(Schemas.Raw.Tables.collection())).toStream()
                    .map(js -> js.getString(Schemas.Raw.Tables.table.name(), ""))
                    .distinct()
                    .collect(Collectors.toList());
        }

    }
}
