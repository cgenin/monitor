package net.christophe.genin.domain.server.model;

import net.christophe.genin.domain.server.adapter.mysql.MysqlConfiguration;
import net.christophe.genin.domain.server.adapter.mysql.MysqlProject;
import net.christophe.genin.domain.server.adapter.nitrite.NitriteConfiguration;
import net.christophe.genin.domain.server.adapter.nitrite.NitriteProject;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import org.dizitart.no2.objects.Id;
import rx.Single;

import java.util.ArrayList;
import java.util.List;

public abstract class Configuration {

    @Id
    private long confId = 0L;

    private String mysqlHost;
    private Integer mysqlPort;
    private String mysqlUser;
    private String mysqlPassword;
    private String mysqlDB;


    private List<String> javaFilters = new ArrayList<>();
    private List<String> npmFilters = new ArrayList<>();

    public static Single<Configuration> load() {
        return (Mysqls.Instance.get().active()) ?
                MysqlConfiguration.load() :
                NitriteConfiguration.load();
    }

    public long confId() {
        return confId;
    }

    public Configuration setConfId(long confId) {
        this.confId = confId;
        return this;
    }

    public List<String> javaFilters() {
        return javaFilters;
    }

    public Configuration setJavaFilters(List<String> javaFilters) {
        this.javaFilters = javaFilters;
        return this;

    }

    public List<String> npmFilters() {
        return npmFilters;
    }

    public Configuration setNpmFilters(List<String> npmFilters) {
        this.npmFilters = npmFilters;
        return this;

    }

    public String mysqlHost() {
        return mysqlHost;
    }

    public Configuration setMysqlHost(String mysqlHost) {
        this.mysqlHost = mysqlHost;
        return this;

    }

    public Integer mysqlPort() {
        return mysqlPort;
    }

    public Configuration setMysqlPort(Integer mysqlPort) {
        this.mysqlPort = mysqlPort;
        return this;

    }

    public String mysqlUser() {
        return mysqlUser;
    }

    public Configuration setMysqlUser(String mysqlUser) {
        this.mysqlUser = mysqlUser;
        return this;

    }

    public String mysqlPassword() {
        return mysqlPassword;
    }

    public Configuration setMysqlPassword(String mysqlPassword) {
        this.mysqlPassword = mysqlPassword;
        return this;

    }

    public String mysqlDB() {
        return mysqlDB;

    }

    public Configuration setMysqlDB(String mysqlDB) {
        this.mysqlDB = mysqlDB;
        return this;

    }
}
