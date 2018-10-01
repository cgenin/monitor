package net.christophe.genin.monitor.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.FrontApps;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IFrontApps;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class MysqlFrontApps extends FrontApps {

    private final MysqlIFrontApps port;

    public static class MysqlIFrontApps implements IFrontApps {
        private final Mysqls mysqls;

        public MysqlIFrontApps(Mysqls mysqls) {
            this.mysqls = mysqls;
        }

        @Override
        public FrontApps newInstance(JsonObject packagesJson, String groupId, String artifactId, String version) {
            return new MysqlFrontApps(this, packagesJson, groupId, artifactId, version, new Date().getTime());
        }

        @Override
        public Observable<FrontApps> findBy(String groupId, String artifactId, String version) {

            return mysqls.select("SELECT ID, LATEST, document  from FRONT_APPS WHERE GROUP_ID=? AND ARTIFACT_ID=? AND VERSION=?", new JsonArray().add(groupId).add(artifactId).add(version))
                    .first()
                    .flatMap(resultSet -> Observable.from(resultSet.getResults()))
                    .map(arr -> {
                        String id = arr.getString(0);
                        Long latest = arr.getLong(1);
                        String strJson = arr.getString(2);
                        JsonObject packagesJson = new JsonObject(strJson);
                        return newInstance(packagesJson, groupId, artifactId, version)
                                .setId(id)
                                .setLastUpdate(latest);
                    });
        }

        private Single<Boolean> insert(MysqlFrontApps frontApps) {
            String encode = frontApps.packagesJson().encode();
            JsonArray params = new JsonArray().add(frontApps.id()).add(frontApps.groupId()).add(frontApps.artifactId()).add(frontApps.lastUpdate()).add(frontApps.version()).add(encode);
            return mysqls.execute("INSERT INTO front_apps (ID, GROUP_ID, ARTIFACT_ID, LATEST, VERSION, document) VALUES (?,?,?,?,?, ?)", params)
                    .map(updateResult -> updateResult.getUpdated() == 1);
        }

        private Single<Boolean> update(MysqlFrontApps fa) {
            long lastUpdate = fa.lastUpdate();
            String encode = fa.packagesJson().encode();
            String id = fa.id();
            JsonArray params = new JsonArray().add(lastUpdate).add(encode).add(id);
            return mysqls.execute("update FRONT_APPS SET LATEST=?, document=? WHERE ID=?", params)
                    .map(updateResult -> updateResult.getUpdated() == 1);
        }

        @Override
        public Observable<FrontApps> findAll() {
           return mysqls.select("SELECT ID, LATEST, document,GROUP_ID, ARTIFACT_ID,VERSION   from FRONT_APPS ")
                    .flatMap(resultSet -> Observable.from(resultSet.getResults()))
                    .map(arr -> {
                        String id = arr.getString(0);
                        Long latest = arr.getLong(1);
                        String strJson = arr.getString(2);
                        String groupId = arr.getString(3);
                        String artifactId = arr.getString(4);
                        String version = arr.getString(5);
                        JsonObject packagesJson = new JsonObject(strJson);
                        return newInstance(packagesJson, groupId, artifactId, version)
                                .setId(id)
                                .setLastUpdate(latest);
                    });
        }
    }

    private MysqlFrontApps(MysqlIFrontApps port, JsonObject packagesJson, String groupId, String artifactId, String version, long lastUpdate) {
        super(packagesJson, groupId, artifactId, version, lastUpdate);
        this.port = port;
    }

    @Override
    public Single<Boolean> save() {

        return Single.just(this)
                .subscribeOn(Schedulers.io())
                .flatMap(fa -> {
                    if (Objects.isNull(fa.id())) {
                        String newId = UUID.randomUUID().toString();
                        fa.setId(newId);
                        return port.insert(fa);
                    }

                    return port.update(fa);
                });
    }
}
