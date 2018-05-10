package net.christophe.genin.domain.server.adapter.nitrite;

import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.model.Configuration;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.Objects;

public class NitriteConfiguration extends Configuration {

    public NitriteConfiguration() {
    }

    public NitriteConfiguration(Configuration configuration) {
        super(configuration);
    }

    public static class ConfigurationHandler {

        private final Dbs dbs;

        @SuppressWarnings("unchecked")
        Configuration toModel(Document document) {
            if (Objects.isNull(document))
                return new NitriteConfiguration();
            return new NitriteConfiguration()
                    .setConfId(document.get("confId", Long.class))
                    .setMysqlHost(document.get("mysqlHost", String.class))
                    .setMysqlPort(document.get("mysqlPort", Integer.class))
                    .setMysqlUser(document.get("mysqlUser", String.class))
                    .setMysqlPassword(document.get("mysqlPassword", String.class))
                    .setMysqlDB(document.get("mysqlDB", String.class))
                    .setJavaFilters(document.get("javaFilters", List.class))
                    .setNpmFilters(document.get("npmFilters", List.class));
        }

        Document toDocument(Configuration configuration) {
            return Document.createDocument("confId", configuration.getConfId())
                    .put("mysqlHost", configuration.getMysqlHost())
                    .put("mysqlPort", configuration.getMysqlPort())
                    .put("mysqlUser", configuration.getMysqlUser())
                    .put("mysqlPassword", configuration.getMysqlPassword())
                    .put("mysqlDB", configuration.getMysqlDB())
                    .put("javaFilters", configuration.getJavaFilters())
                    .put("npmFilters", configuration.getNpmFilters());
        }

        public ConfigurationHandler(Dbs dbs) {
            this.dbs = dbs;
        }

        public NitriteCollection getCollection() {
            return dbs.getCollection("ConfigurationDatabase");
        }

        public Single<Configuration> load() {

            return Single.fromCallable(() ->
                    getCollection().find().firstOrDefault())
                    .map(this::toModel);
        }

        public Single<Boolean> save(Configuration conf) {
            if (Objects.isNull(conf.getConfId())) {
                conf.setConfId(1L);
            }
            return Single.just(conf)
                    .subscribeOn(Schedulers.io())
                    .map(this::toDocument)
                    .map(doc -> getCollection().update(doc, true))
                    .map(wr -> wr.getAffectedCount() == 1);

        }
    }


}
