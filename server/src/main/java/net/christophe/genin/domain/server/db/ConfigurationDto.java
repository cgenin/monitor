package net.christophe.genin.domain.server.db;

import org.dizitart.no2.objects.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Bean représentant la configuration du serveur.
 */
public class ConfigurationDto implements Serializable {


    @Id
    private long confId = 0L;

    private String mysqlHost;
    private Integer mysqlPort;
    private String mysqlUser;
    private String mysqlPassword;
    private String mysqlDB;
    private String moniThorUrl;


    private List<String> javaFilters = new ArrayList<>();
    private List<String> npmFilters = new ArrayList<>();

    public long getConfId() {
        return confId;
    }

    public void setConfId(long confId) {
        this.confId = confId;
    }

    public List<String> getJavaFilters() {
        return javaFilters;
    }

    public void setJavaFilters(List<String> javaFilters) {
        this.javaFilters = javaFilters;
    }

    public List<String> getNpmFilters() {
        return npmFilters;
    }

    public void setNpmFilters(List<String> npmFilters) {
        this.npmFilters = npmFilters;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public void setMysqlHost(String mysqlHost) {
        this.mysqlHost = mysqlHost;
    }

    public Integer getMysqlPort() {
        return mysqlPort;
    }

    public void setMysqlPort(Integer mysqlPort) {
        this.mysqlPort = mysqlPort;
    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public void setMysqlUser(String mysqlUser) {
        this.mysqlUser = mysqlUser;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public void setMysqlPassword(String mysqlPassword) {
        this.mysqlPassword = mysqlPassword;
    }

    public String getMysqlDB() {
        return mysqlDB;
    }

    public void setMysqlDB(String mysqlDB) {
        this.mysqlDB = mysqlDB;
    }

    public String getMoniThorUrl() {
        return moniThorUrl;
    }

    public void setMoniThorUrl(String moniThorUrl) {
        this.moniThorUrl = moniThorUrl;
    }
}
