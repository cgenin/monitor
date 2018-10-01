package net.christophe.genin.monitor.domain.server.model;

import java.util.List;

/**
 * The model for the configuration of the server.
 */
public interface  Configuration {

    Long confId();

    Configuration setConfId(Long confId);

    List<String> javaFilters();

    Configuration setJavaFilters(List<String> javaFilters);

    List<String> npmFilters();

    Configuration setNpmFilters(List<String> npmFilters);

    String mysqlHost();

    Configuration setMysqlHost(String mysqlHost);

    Integer mysqlPort();

    Configuration setMysqlPort(Integer mysqlPort);

    String mysqlUser();

    Configuration setMysqlUser(String mysqlUser);

    String mysqlPassword();

    Configuration setMysqlPassword(String mysqlPassword);

    String mysqlDB();

    Configuration setMysqlDB(String mysqlDB);

    List<String> ignoreJava();

    Configuration setIgnoreJava(List<String> ignoreJava);

    String monithorUrl();

    Configuration setMonithorUrl(String monithorUrl);
}
