package net.christophe.genin.domain.server.db;

import io.vertx.core.json.JsonObject;

public final class Schemas {
    public static final String RAW_COLLECTION = "Apps-Store";
    public static final String RAW_STATE = "state";

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
        id, name, release, snapshot, tables, javaDeps, npmDeps, latestUpdate;

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
}
