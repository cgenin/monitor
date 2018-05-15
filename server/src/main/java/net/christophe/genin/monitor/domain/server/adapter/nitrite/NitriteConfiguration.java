package net.christophe.genin.monitor.domain.server.adapter.nitrite;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.db.nitrite.NitriteDbs;
import net.christophe.genin.monitor.domain.server.model.Configuration;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.WriteResult;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.dizitart.no2.filters.Filters.eq;

public class NitriteConfiguration extends Configuration {

    private static final Logger logger = LoggerFactory.getLogger(NitriteConfiguration.class);

    public NitriteConfiguration() {
    }


    public static class ConfigurationHandler {

        private final NitriteDbs nitriteDbs;

        @SuppressWarnings("unchecked")
        Configuration toModel(Document document) {
            if (Objects.isNull(document))
                return new NitriteConfiguration();
            List javaFilters = document.get("javaFilters", List.class);
            List npmFilters = document.get("npmFilters", List.class);
            return new NitriteConfiguration()
                    .setConfId(document.get("confId", Long.class))
                    .setMonithorUrl(document.get("monithorUrl", String.class))
                    .setMysqlHost(document.get("mysqlHost", String.class))
                    .setMysqlPort(document.get("mysqlPort", Integer.class))
                    .setMysqlUser(document.get("mysqlUser", String.class))
                    .setMysqlPassword(document.get("mysqlPassword", String.class))
                    .setMysqlDB(document.get("mysqlDB", String.class))
                    .setJavaFilters(javaFilters)
                    .setNpmFilters(npmFilters);
        }

        Document toDocument(Configuration configuration) {
            List<String> npmFilters = new ArrayList<>(configuration.npmFilters());
            List<String> javaFilters = new ArrayList<>(configuration.javaFilters());
            return Document.createDocument("confId", configuration.confId())
                    .put("monithorUrl", configuration.monithorUrl())
                    .put("mysqlHost", configuration.mysqlHost())
                    .put("mysqlPort", configuration.mysqlPort())
                    .put("mysqlUser", configuration.mysqlUser())
                    .put("mysqlPassword", configuration.mysqlPassword())
                    .put("mysqlDB", configuration.mysqlDB())
                    .put("javaFilters", javaFilters)
                    .put("npmFilters", npmFilters);
        }

        public ConfigurationHandler(NitriteDbs nitriteDbs) {
            this.nitriteDbs = nitriteDbs;
        }

        public NitriteCollection getCollection() {
            return nitriteDbs.getCollection("DatabaseConfiguration");
        }

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


}
