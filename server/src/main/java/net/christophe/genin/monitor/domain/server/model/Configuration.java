package net.christophe.genin.monitor.domain.server.model;

import net.christophe.genin.monitor.domain.server.adapter.nitrite.NitriteConfiguration;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import org.dizitart.no2.objects.Id;
import rx.Single;

import java.util.ArrayList;
import java.util.List;

public abstract class Configuration {

    @Id
    private Long    confId = 1L;

    private String  mysqlHost;
    private Integer mysqlPort;
    private String  mysqlUser;
    private String  mysqlPassword;
    private String  mysqlDB;


    private List<String> javaFilters = new ArrayList<>();
    private List<String> npmFilters = new ArrayList<>();
    private List<String> ignoreJava = new ArrayList<>();

    public static Single<Configuration> load() {
        return new NitriteConfiguration.ConfigurationHandler(NitriteDbs.instance).load();
    }

    public static Single<Boolean> save(Configuration conf) {
        return new NitriteConfiguration.ConfigurationHandler(NitriteDbs.instance).save(conf);
    }

    public Configuration() {
    }


    public Long confId() {
        return confId;
    }

    public Configuration setConfId(Long confId) {
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

    public List<String> ignoreJava() {
        return ignoreJava;
    }

    public Configuration setIgnoreJava(List<String> ignoreJava) {
        this.ignoreJava = ignoreJava;
        return this;
    }
}
