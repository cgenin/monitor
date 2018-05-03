package net.christophe.genin.domain.server.db;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.db.mysql.MysqlCommand;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.db.nitrite.NitriteCommand;
import net.christophe.genin.domain.server.json.Jsons;
import rx.Observable;
import rx.Single;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Commands {

    static Commands get() {
        return (!Mysqls.Instance.get().active()) ? new NitriteCommand()
                : new MysqlCommand();
    }

    Single<String> reset();

    Observable<String> projects(JsonObject json, String artifactId);

    Observable<String> tables(List<String> tables, String artifactId, long update);

    Observable<String> versions(JsonObject json, String artifactId, String version);

    Observable<String> apis(JsonObject apis, String artifactId, String version, long update, JsonArray services);

    default Observable<JsonObject> servicesToJson(JsonArray services) {
        Function<JsonObject, Stream<? extends JsonObject>> convert2Json = obj -> {
            String className = obj.getString(Schemas.Raw.Apis.Services.name.name(), "");
            JsonArray methods = obj.getJsonArray(Schemas.Raw.Apis.Services.methods.name(), new JsonArray());
            return Jsons.builder(methods)
                    .toStream()
                    .map(o -> o.put("className", className));
        };
        return Observable.from(
                Jsons.builder(services).toStream().flatMap(convert2Json).collect(Collectors.toList())
        );
    }

    Observable<String> dependencies(JsonObject json, String artifactId, ConfigurationDto configuration);

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

    class DependenciesSanitizer {

        private static Pattern[] PATTERNS = new Pattern[]{
                Pattern.compile("\\-MANAGER"),
                Pattern.compile("\\-SERVICE"),
                Pattern.compile("\\-IMPL"),
                Pattern.compile("\\-CLIENT"),
        };
        private final String str;

        public DependenciesSanitizer(String str) {
            this.str = str;
        }

        private String innerRun(String chaine, Pattern[] regexps) {
            if (regexps.length == 0) {
                return chaine;
            }
            String newChaine = regexps[0].matcher(chaine).replaceAll("");
            Pattern[] newPatterns = (regexps.length == 1) ? new Pattern[0] : Arrays.copyOfRange(regexps, 1, regexps.length);
            return innerRun(newChaine, newPatterns);

        }

        public String run() {
            String US = str.toUpperCase();
            return innerRun(US, PATTERNS);
        }
    }
}
