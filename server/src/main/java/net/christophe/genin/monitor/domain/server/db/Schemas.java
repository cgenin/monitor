package net.christophe.genin.monitor.domain.server.db;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.monitor.addon.json.Jsons;

/**
 * description of the datas.
 */
public final class Schemas {
    public static final String RAW_COLLECTION = "Apps-Store";
    public static final String FRONT_COLLECTION = "Front-Apps-Store";
    public static final String RAW_STATE = "state";

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
                    name, method, returns, path, params, comment
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
