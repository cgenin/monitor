package net.christophe.genin.monitor.domain.server.base;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.DownloadConfig;
import com.wix.mysql.config.MysqldConfig;
import io.vertx.core.json.JsonObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.sql.SQLException;

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
    protected static JsonObject config = new JsonObject()
            .put("host", HOST_DB)
            .put("port", PORT_DB)
            .put("username", USER_DB)
            .put("password", PWD_DB)
            .put("database", NAM_DB);
    private static ThreadLocal<EmbeddedMysql> server = new ThreadLocal<>();

    public synchronized static void startDbServer() throws SQLException {
        MysqldConfig cfg = aMysqldConfig(v5_7_latest)
                .withUser(USER_DB, PWD_DB)
                .withPort(PORT_DB)
                .build();
        EmbeddedMysql.Builder builder = new EmbeddedMysql.Builder(cfg, new DownloadConfig.Builder()
                .withProxy(aHttpProxy("mutpoit", 8085)).build())
                .addSchema(NAM_DB);
        server.set(builder.start());


    }

    public synchronized static void stopDbServer() {
        server.get().stop();
        server.remove();
    }
}
