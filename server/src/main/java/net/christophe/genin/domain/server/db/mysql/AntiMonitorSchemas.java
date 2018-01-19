package net.christophe.genin.domain.server.db.mysql;

import rx.Single;


public class AntiMonitorSchemas {

    private static String[] CREATE_SCRIPTS = new String[]{
            "CREATE TABLE IF NOT EXISTS EVENTS (\n" +
                    "  ID       BIGINT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  state    INTEGER,\n" +
                    "  document LONGTEXT\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS TABLES (\n" +
                    "  ID      VARCHAR(100) NOT NULL PRIMARY KEY,\n" +
                    "  NAME    TEXT NOT NULL,\n" +
                    "  SERVICE TEXT NOT NULL,\n" +
                    "  latestUpdate BIG INT\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS TABLES (\n" +
                    "  ID       VARCHAR(767) PRIMARY KEY,\n" +
                    "  NAME     VARCHAR(100),\n" +
                    "  document LONGTEXT\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS APIS (\n" +
                    "  ID        VARCHAR(767) PRIMARY KEY,\n" +
                    "  METHOD   VARCHAR(10),\n" +
                    "  FULLURL   TEXT,\n" +
                    "  IDPROJECT VARCHAR(1000),\n" +
                    "  document LONGTEXT\n" +
                    ")",

            "CREATE TABLE IF NOT EXISTS PROJECTS (\n" +
                    "  ID        VARCHAR(767) PRIMARY KEY,\n" +
                    "  NAME      VARCHAR(200),\n" +
                    "  document  LONGTEXT\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS VERSIONS (\n" +
                    "  ID        VARCHAR(1000) PRIMARY KEY,\n" +
                    "  IDPROJECT VARCHAR(1000),\n" +
                    "  NAME      VARCHAR(200),\n" +
                    "  document  LONGTEXT\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS DEPENDENCIES (\n" +
                    "  RESOURCE VARCHAR(200),\n" +
                    "  USED_BY  VARCHAR(200),\n" +
                    "  document LONGTEXT,\n" +
                    "  PRIMARY KEY (RESOURCE, USED_BY)\n" +
                    ")",
            "DELETE FROM EVENTS",
            "DELETE FROM PROJECTS",
            "DELETE FROM TABLES",
            "DELETE FROM APIS",
            "DELETE FROM VERSIONS",
            "DELETE FROM DEPENDENCIES"
    };

    private static String[] DELETE_SCRIPTS=new String[]{
            "DELETE FROM  PROJECTS",
            "DELETE FROM TABLES",
            "DELETE FROM APIS",
            "DELETE FROM VERSIONS",
            "DELETE FROM DEPENDENCIES"
    };


    public static Single<String> create() {
        return Mysqls.Instance.get()
                .batch(CREATE_SCRIPTS)
                .map(list -> "Creation of EVENTS, PROJECTS, TABLES, API, VERSIONS if not exist :" + list);
    }

    public static Single<String> delete() {
        return Mysqls.Instance.get()
                .batch(DELETE_SCRIPTS)
                .map(list -> "Suppress of PROJECTS, TABLES, API, VERSIONS :" + list);
    }

}
