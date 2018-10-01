package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IConfiguration;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Configuration;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.Id;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.dizitart.no2.filters.Filters.eq;

public class NitriteConfiguration implements Configuration {

    private static final Logger logger = LoggerFactory.getLogger(NitriteConfiguration.class);

    @Id
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

    public NitriteConfiguration() {
    }


    public static class ConfigurationHandler implements IConfiguration {

        private final NitriteDbs nitriteDbs;

        @SuppressWarnings("unchecked")
        Configuration toModel(Document document) {
            if (Objects.isNull(document))
                return new NitriteConfiguration();
            List javaFilters = document.get("javaFilters", List.class);
            List npmFilters = document.get("npmFilters", List.class);
            return new NitriteConfiguration()
                    .setConfId(document.get("confId", Long.class))
                    .setMysqlHost(document.get("mysqlHost", String.class))
                    .setMysqlPort(document.get("mysqlPort", Integer.class))
                    .setMysqlUser(document.get("mysqlUser", String.class))
                    .setMysqlPassword(document.get("mysqlPassword", String.class))
                    .setMysqlDB(document.get("mysqlDB", String.class))
                    .setMonithorUrl(document.get("monithorUrl", String.class))
                    .setJavaFilters(javaFilters)
                    .setNpmFilters(npmFilters);
        }

        Document toDocument(Configuration configuration) {
            List<String> npmFilters = new ArrayList<>(configuration.npmFilters());
            List<String> javaFilters = new ArrayList<>(configuration.javaFilters());
            return Document.createDocument("confId", configuration.confId())
                    .put("mysqlHost", configuration.mysqlHost())
                    .put("mysqlPort", configuration.mysqlPort())
                    .put("mysqlUser", configuration.mysqlUser())
                    .put("mysqlPassword", configuration.mysqlPassword())
                    .put("mysqlDB", configuration.mysqlDB())
                    .put("monithorUrl", configuration.monithorUrl())
                    .put("javaFilters", javaFilters)
                    .put("npmFilters", npmFilters);
        }

        public ConfigurationHandler(NitriteDbs nitriteDbs) {
            this.nitriteDbs = nitriteDbs;
        }

        public NitriteCollection getCollection() {
            return nitriteDbs.getCollection("DatabaseConfiguration");
        }

        @Override
        public Single<Configuration> load() {

            return Single.fromCallable(() ->
                    getCollection().find(eq("confId", 1L)))
                    .map(cursor -> {
                        List<Document> l =  cursor.toList();
                        if(l.isEmpty())
                            return new NitriteConfiguration();
                        return toModel(l.get(0));
                    });
        }

        @Override
        public Single<Boolean> save(Configuration conf) {
            WriteResult writeResult = getCollection().remove(eq("confId", 1L));
            logger.debug("nb deleted conf : " + writeResult.getAffectedCount());

            return Single.just(conf.setConfId(1L))
                    .subscribeOn(Schedulers.io())
                    .map(this::toDocument)
                    .map(doc -> getCollection().update(doc, true))
                    .map(wr -> wr.getAffectedCount() == 1);

        }
    }


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
