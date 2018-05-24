package net.christophe.genin.monitor.domain.server.base;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.ScriptResolver;
import com.wix.mysql.config.DownloadConfig;
import com.wix.mysql.config.MysqldConfig;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.Database;
import net.christophe.genin.monitor.domain.server.adapter.nitrite.NitriteConfiguration;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.model.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.config.ProxyFactory.aHttpProxy;
import static com.wix.mysql.distribution.Version.v5_7_latest;

public class DbTest {

    @BeforeClass
    public static void sBefore() throws SQLException {
        startDbServer();
    }

    @AfterClass
    public static void sAfter() {
        stopDbServer();
    }

    public static final String HOST_DB = "localhost";
    public static final int PORT_DB = 8043;
    public static final String USER_DB = "vertx";
    public static final String PWD_DB = "password";
    public static final String NAM_DB = "antimonitor";
    public static JsonObject config = new JsonObject()
            .put("host", HOST_DB)
            .put("port", PORT_DB)
            .put("username", USER_DB)
            .put("password", PWD_DB)
            .put("database", NAM_DB);
    private static ThreadLocal<EmbeddedMysql> server = new ThreadLocal<>();

    public synchronized static void startDbServer() {
        EmbeddedMysql.Builder builder = getBuilder().addSchema(NAM_DB);
        server.set(builder.start());


    }

    private static EmbeddedMysql.Builder getBuilder() {
        MysqldConfig cfg = aMysqldConfig(v5_7_latest)
                .withUser(USER_DB, PWD_DB)
                .withPort(PORT_DB)
                .build();
        DownloadConfig.Builder build = Optional.ofNullable(System.getProperty("test.proxy.host"))
                .map(host -> {
                    String p = System.getProperty("test.proxy.port");
                    Integer port = Integer.valueOf(p);
                    return new DownloadConfig.Builder()
                            .withProxy(aHttpProxy(host, port));
                })
                .orElse(new DownloadConfig.Builder());
        return new EmbeddedMysql.Builder(cfg, build.build());
    }

    public synchronized static void startDbServerWithSchema() {
        EmbeddedMysql.Builder builder = getBuilder()
                .addSchema(NAM_DB, ScriptResolver.classPathScript("sql/CREATE_SCHEMA.sql"));
        server.set(builder.start());


    }

    public void setAntiMonitorDS(TestContext context, Async async, Vertx vertx) {
        Mysqls.Instance.disabled();
        Configuration conf = new NitriteConfiguration().setMysqlHost(HOST_DB)
                .setMysqlDB(NAM_DB)
                .setMysqlPassword(PWD_DB)
                .setMysqlPort(PORT_DB)
                .setMysqlUser(USER_DB);
        Configuration.save(conf).subscribe(
                bool -> {
                    context.assertTrue(bool);
                    async.countDown();
                    vertx.eventBus().<JsonObject>send(Database.MYSQL_ON_OFF, new JsonObject(), msg -> {
                        JsonObject body = msg.result().body();
                        context.assertNotNull(body);
                        context.assertEquals(true, body.getBoolean("active"));
                        async.countDown();
                    });
                },
                context::fail
        );
    }


    public synchronized static void stopDbServer() {
        server.get().stop();
        server.remove();
    }
}
