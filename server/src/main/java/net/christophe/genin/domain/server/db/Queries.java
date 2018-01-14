package net.christophe.genin.domain.server.db;

import io.vertx.core.json.JsonArray;
import net.christophe.genin.domain.server.db.mysql.MysqlQuery;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.db.nitrite.NitriteQuery;
import rx.Single;

/**
 * Interface de lecture des données.
 */
public interface Queries {
    /**
     * Getter sur l'implémentation courante.
     * @return l'implémentation.
     */
    static Queries get() {
        return (Mysqls.Instance.get().active()) ? new MysqlQuery() : new NitriteQuery();
    }

    /**
     * Find all projects
     * @return le résultat
     */
    Single<JsonArray> projects();

    /**
     * find all tables
     * @return le résultat
     */
    Single<JsonArray> tables();

    /**
     * find all apis.
     * @return le résultat
     */
    Single<JsonArray> apis();

    /**
     * Find all version for one projects.
     * @param idProject id du projet
     * @return le résultat
     */
    Single<JsonArray> versions(String idProject);


    Single<JsonArray> listAllResourceDependencies();

    Single<JsonArray> usedBy(String resource);
}
