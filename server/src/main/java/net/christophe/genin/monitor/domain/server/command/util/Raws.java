package net.christophe.genin.monitor.domain.server.command.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.monitor.domain.server.db.Schemas;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Util function for Raw.
 */
public final class Raws {

    /**
     * Test if the version is an snapshot or not.
     *
     * @param version the version tag.
     * @return true if it's an snapshot.
     */
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

    /**
     * Extract from Json Object the name of the tables.
     *
     * @param json the Json
     * @return the list of table's name.
     */
    public static List<String> extractTables(JsonObject json) {
        return Jsons.builder(json.getJsonArray(Schemas.Raw.Tables.collection())).toStream()
                .map(js -> js.getString(Schemas.Raw.Tables.table.name(), ""))
                .distinct()
                .collect(Collectors.toList());
    }


}
