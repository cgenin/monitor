package net.christophe.genin.monitor.domain.server.base;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.sql.SQLException;

public class DbWithSchemaTest extends BaseDbTest {

    @BeforeClass
    public static void sBefore() throws SQLException {
        startDbServerWithSchema();
    }

    @AfterClass
    public static void sAfter() {
        stopDbServer();
    }

    protected Handler<AsyncResult<String>> callbackForSchemaCreation(Vertx vertx, TestContext context, Async async) {
        return msg -> {
            context.assertTrue(msg.succeeded());
            Mysqls res = Mysqls.Instance.set(vertx, DbTest.config.put("user", DbTest.USER_DB));
            context.assertTrue(res.active());
            async.complete();
        };
    }

}
