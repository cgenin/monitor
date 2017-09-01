package net.christophe.genin.domain.server.db;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.json.Jsons;

import java.util.List;
import java.util.stream.Collectors;

public final class Schemas {
    public static final String RAW_COLLECTION = "Apps-Store";
    public static final String RAW_STATE = "state";

    public static class Configuration {

        public static JsonObject toJson(ConfigurationDto configurationDto) {
            return new JsonObject()
                    .put("id", configurationDto.getConfId())
                    .put("javaFilters", configurationDto.getJavaFilters()
                            .parallelStream().collect(Jsons.Collectors.toJsonArray()))
                    .put("npmFilters", configurationDto.getNpmFilters()
                            .parallelStream().collect(Jsons.Collectors.toJsonArray()));
        }

        @SuppressWarnings("unchecked")
        public static ConfigurationDto fromJson(JsonObject obj) {
            ConfigurationDto configurationDto = new ConfigurationDto();
            configurationDto.setConfId(obj.getLong("id", 0L));
            configurationDto.setJavaFilters(Jsons.builder(obj.getJsonArray("javaFilters")).toListString());
            configurationDto.setNpmFilters(Jsons.builder(obj.getJsonArray("npmFilters")).toListString());
            return configurationDto;
        }
    }

    public enum Raw {
        groupId, artifactId, version, dependencies, update;

        public enum Tables {
            table, className;

            public static String collection() {
                return Tables.class.getSimpleName().toLowerCase();
            }
        }

        public enum Dependencies {
            groupId, artifactId, version;

            public static String collection() {
                return Dependencies.class.getSimpleName().toLowerCase();
            }
        }


    }

    public enum Projects {
        id, name, release, snapshot, tables, javaDeps, npmDeps, latestUpdate, changelog, apis;

        public static String collection() {
            return Projects.class.getSimpleName().toLowerCase();
        }
    }

    public enum Tables {
        id, name, latestUpdate, services;

        public static String collection() {
            return Tables.class.getSimpleName().toLowerCase();
        }
    }

    public enum Version {
        id, name, isSnapshot, tables, javaDeps, npmDeps, latestUpdate, changelog;

        public static final String PREFIX = "version/";

        public static String collection(String id) {
            return PREFIX + id;
        }
    }
}
