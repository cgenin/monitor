package net.christophe.genin.domain.server.db.mysql;

import rx.Single;

import java.util.Arrays;

public class AntiMonitorSchemas {

    private static String[] CREATE_SCRIPTS = new String[]{
            "CREATE TABLE IF NOT EXISTS EVENTS (\n" +
                    "  ID       BIGINT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  state    INTEGER,\n" +
                    "  document JSON\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS TABLES\n" +
                    "(  ID      VARCHAR(100) NOT NULL PRIMARY KEY,\n" +
                    "  NAME    TEXT NOT NULL,\n" +
                    "  SERVICE TEXT NOT NULL,\n" +
                    "  latestUpdate BIGINT\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS TABLES (\n" +
                    "  ID       VARCHAR(1000) PRIMARY KEY,\n" +
                    "  NAME     VARCHAR(100),\n" +
                    "  document JSON\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS API (\n" +
                    "  ID        VARCHAR(1000) PRIMARY KEY,\n" +
                    "  FULLURL   TEXT,\n" +
                    "  IDPROJECT VARCHAR(1000),\n" +
                    "  document  JSON\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS VERSIONS (\n" +
                    "  ID        VARCHAR(1000) PRIMARY KEY,\n" +
                    "  IDPROJECT VARCHAR(1000),\n" +
                    "  NAME      VARCHAR(200),\n" +
                    "  document  JSON\n" +
                    ")",
            "DELETE FROM EVENTS",
            "DELETE FROM  PROJECTS",
            "DELETE FROM TABLES",
            "DELETE FROM API",
            "DELETE FROM VERSIONS"

    };


    public static Single<String> create() {
        return Mysqls.Instance.get()
                .batch( CREATE_SCRIPTS)
                .map(list -> "Creation of EVENTS, PROJECTS, TABLES, API, VERSIONS if not exist :" + list);
    }

}
