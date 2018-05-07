package net.christophe.genin.domain.server.db;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.monitor.addon.json.Jsons;

/**
 * description of the datas.
 */
public final class Schemas {
    public static final String RAW_COLLECTION = "Apps-Store";
    public static final String FRONT_COLLECTION = "Front-Apps-Store";
    public static final String RAW_STATE = "state";

    /**
     * ConfigurationQuery Dto builder / parser.
     */
    public static class Configuration {

        public static JsonObject toJson(ConfigurationDto configurationDto) {
            JsonObject mysql = new JsonObject()
                    .put("host", configurationDto.getMysqlHost())
                    .put("port", configurationDto.getMysqlPort())
                    .put("user", configurationDto.getMysqlUser())
                    .put("password", configurationDto.getMysqlPassword())
                    .put("database", configurationDto.getMysqlDB());
            return new JsonObject()
                    .put("id", configurationDto.getConfId())
                    .put("javaFilters", configurationDto.getJavaFilters()
                            .parallelStream().collect(Jsons.Collectors.toJsonArray()))
                    .put("npmFilters", configurationDto.getNpmFilters()
                            .parallelStream().collect(Jsons.Collectors.toJsonArray()))
                    .put("mysql", mysql);
        }

        @SuppressWarnings("unchecked")
        public static ConfigurationDto fromJson(JsonObject obj) {
            ConfigurationDto configurationDto = new ConfigurationDto();
            JsonObject mysql = obj.getJsonObject("mysql", new JsonObject());
            configurationDto.setConfId(obj.getLong("id", 0L));
            configurationDto.setMysqlHost(mysql.getString("host"));
            configurationDto.setMysqlPort(mysql.getInteger("port"));
            configurationDto.setMysqlUser(mysql.getString("user"));
            configurationDto.setMysqlPassword(mysql.getString("password"));
            configurationDto.setMysqlDB(mysql.getString("database"));
            configurationDto.setJavaFilters(Jsons.builder(obj.getJsonArray("javaFilters")).toListString());
            configurationDto.setNpmFilters(Jsons.builder(obj.getJsonArray("npmFilters")).toListString());
            return configurationDto;
        }
    }

    public enum Raw {
        groupId, artifactId, version, dependencies, update, apis;

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

        public enum Apis {
            artifactId, groupId, version, services;

            public enum Services {
                name, methods;

                public enum methods {
                    name, method, returns, path, params, comment;
                }
            }
        }

    }

    public enum Projects {
        id, name, release, snapshot, tables, javaDeps, npmDeps, latestUpdate, changelog, apis;

        public static String collection() {
            return Projects.class.getSimpleName().toLowerCase();
        }
    }

    public enum Apis {
        id, artifactId, groupId, name, method, returns, path, params, comment, since, className, latestUpdate, idProject;

        public static String collection() {
            return Apis.class.getSimpleName().toLowerCase();
        }
    }

    public enum Tables {
        id, name, latestUpdate, services;

        public static String collection() {
            return Tables.class.getSimpleName().toLowerCase();
        }
    }

    public enum Dependency {
        id, usedBy, resource;

        public static final String PREFIX = "dependency";



        public static String collection() {
            return PREFIX;
        }
    }

    public enum Version {
        id, name, idproject, isSnapshot, tables, javaDeps, npmDeps, latestUpdate, apis, changelog;

        public static final String PREFIX = "version2/";



        public static String collection() {
            return PREFIX + "Root";
        }
    }
}
