package net.christophe.genin.monitor.domain.server.verticle;

import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Configuration;

import java.util.ArrayList;
import java.util.List;

public class MockConfiguration implements Configuration {

        private Long confId = 1L;

        private String mysqlHost;
        private Integer mysqlPort;
        private String mysqlUser;
        private String mysqlPassword;
        private String mysqlDB;
        private String monithorUrl;


        private List<String> javaFilters = new ArrayList<>();
        private List<String> npmFilters = new ArrayList<>();
        private List<String> ignoreJava = new ArrayList<>();



        @Override
        public Long confId() {
            return confId;
        }

        @Override
        public Configuration setConfId(Long confId) {
            this.confId = confId;
            return this;
        }

        @Override
        public List<String> javaFilters() {
            return javaFilters;
        }

        @Override
        public Configuration setJavaFilters(List<String> javaFilters) {
            this.javaFilters = javaFilters;
            return this;

        }

        @Override
        public List<String> npmFilters() {
            return npmFilters;
        }

        @Override
        public Configuration setNpmFilters(List<String> npmFilters) {
            this.npmFilters = npmFilters;
            return this;

        }

        @Override
        public String mysqlHost() {
            return mysqlHost;
        }

        @Override
        public Configuration setMysqlHost(String mysqlHost) {
            this.mysqlHost = mysqlHost;
            return this;

        }

        @Override
        public Integer mysqlPort() {
            return mysqlPort;
        }

        @Override
        public Configuration setMysqlPort(Integer mysqlPort) {
            this.mysqlPort = mysqlPort;
            return this;

        }

        @Override
        public String mysqlUser() {
            return mysqlUser;
        }

        @Override
        public Configuration setMysqlUser(String mysqlUser) {
            this.mysqlUser = mysqlUser;
            return this;

        }

        @Override
        public String mysqlPassword() {
            return mysqlPassword;
        }

        @Override
        public Configuration setMysqlPassword(String mysqlPassword) {
            this.mysqlPassword = mysqlPassword;
            return this;

        }

        @Override
        public String mysqlDB() {
            return mysqlDB;

        }

        @Override
        public Configuration setMysqlDB(String mysqlDB) {
            this.mysqlDB = mysqlDB;
            return this;

        }

        @Override
        public List<String> ignoreJava() {
            return ignoreJava;
        }

        @Override
        public Configuration setIgnoreJava(List<String> ignoreJava) {
            this.ignoreJava = ignoreJava;
            return this;
        }

        @Override
        public String monithorUrl() {
            return monithorUrl;
        }

        @Override
        public Configuration setMonithorUrl(String monithorUrl) {
            this.monithorUrl = monithorUrl;
            return this;
        }
}
