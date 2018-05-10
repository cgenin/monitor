package net.christophe.genin.domain.server.model;

import net.christophe.genin.domain.server.adapter.nitrite.NitriteConfiguration;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
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

    public static Single<Configuration> load() {
        return new NitriteConfiguration.ConfigurationHandler(Dbs.instance).load();
    }

    public static Single<Boolean> save(Configuration conf) {
        return new NitriteConfiguration.ConfigurationHandler(Dbs.instance).save(conf);
    }

    public Configuration() {
    }

    public Configuration(Configuration configuration) {
        confId = configuration.getConfId();
        mysqlHost = configuration.getMysqlHost();
        mysqlPort = configuration.getMysqlPort();
        mysqlUser = configuration.getMysqlUser();
        mysqlPassword = configuration.getMysqlPassword();
        mysqlDB = configuration.getMysqlDB();
    }

    public Long getConfId() {
        return confId;
    }

    public Configuration setConfId(Long confId) {
        this.confId = confId;
        return this;
    }

    public List<String> getJavaFilters() {
        return javaFilters;
    }

    public Configuration setJavaFilters(List<String> javaFilters) {
        this.javaFilters = javaFilters;
        return this;

    }

    public List<String> getNpmFilters() {
        return npmFilters;
    }

    public Configuration setNpmFilters(List<String> npmFilters) {
        this.npmFilters = npmFilters;
        return this;

    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public Configuration setMysqlHost(String mysqlHost) {
        this.mysqlHost = mysqlHost;
        return this;

    }

    public Integer getMysqlPort() {
        return mysqlPort;
    }

    public Configuration setMysqlPort(Integer mysqlPort) {
        this.mysqlPort = mysqlPort;
        return this;

    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public Configuration setMysqlUser(String mysqlUser) {
        this.mysqlUser = mysqlUser;
        return this;

    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public Configuration setMysqlPassword(String mysqlPassword) {
        this.mysqlPassword = mysqlPassword;
        return this;

    }

    public String getMysqlDB() {
        return mysqlDB;

    }

    public Configuration setMysqlDB(String mysqlDB) {
        this.mysqlDB = mysqlDB;
        return this;

    }

}
