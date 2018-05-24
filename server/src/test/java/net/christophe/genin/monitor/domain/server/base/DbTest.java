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
import net.christophe.genin.monitor.domain.server.adapter.Adapters;
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

public class DbTest extends BaseDbTest {

    @BeforeClass
    public static void sBefore() throws SQLException {
        startDbServer();
    }

    @AfterClass
    public static void sAfter() {
        stopDbServer();
    }


}
