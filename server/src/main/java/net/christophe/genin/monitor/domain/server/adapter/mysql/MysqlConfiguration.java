package net.christophe.genin.monitor.domain.server.adapter.mysql;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.monitor.domain.server.model.Configuration;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import rx.Single;

import java.util.Objects;

public class MysqlConfiguration extends Configuration {


    public static Single<Configuration> load() {
        Mysqls mysqls = Mysqls.Instance.get();
        return mysqls.select("SELECT document FROM configuration")
                .map(rs -> {
                    if (Objects.isNull(rs) || rs.getResults().isEmpty()) {
                        return new MysqlConfiguration();
                    }

                    JsonArray firstLine = rs.getResults().get(0);
                    String str = firstLine.getString(0);
                    JsonObject json = new JsonObject(str);
                    return toConfiguration(json);
                }).toSingle();
    }

    private static Configuration toConfiguration(JsonObject json) {
        JsonObject mysql = json.getJsonObject("mysql", new JsonObject());
        return new MysqlConfiguration()
                .setConfId(json.getLong("id", 1L))
                .setMysqlHost(mysql.getString("host"))
                .setMysqlPort(mysql.getInteger("port"))
                .setMysqlUser(mysql.getString("user"))
                .setMysqlPassword(mysql.getString("password"))
                .setMysqlDB(mysql.getString("database"))
                .setJavaFilters(Jsons.builder(json.getJsonArray("getJavaFilters")).toListString())
                .setNpmFilters(Jsons.builder(json.getJsonArray("getNpmFilters")).toListString());

    }


    private MysqlConfiguration() {
    }

}
