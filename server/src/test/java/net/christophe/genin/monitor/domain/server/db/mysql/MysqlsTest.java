package net.christophe.genin.monitor.domain.server.db.mysql;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.DbTest;
import org.junit.*;
import org.junit.runner.RunWith;

import java.sql.SQLException;

@RunWith(VertxUnitRunner.class)
public class MysqlsTest extends DbTest {
    Vertx vertx;



    @Before
    public void before(TestContext context) throws SQLException {
        vertx = Vertx.vertx();
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }


    @Test
    public void should_test_connection_when_valid(TestContext context) {
        Async async = context.async();
        Mysqls.test(vertx, config).subscribe(
                bool -> {
                    context.assertTrue(bool);
                    async.complete();
                },
                context::fail
        );
    }

    @Test
    public void should_not_test_connection_when_invalid(TestContext context) {
        Async async = context.async();
        Mysqls.test(vertx, new JsonObject(config.encode()).put("password", "badpassword")).subscribe(
                bool -> {
                    context.assertFalse(bool);
                    async.complete();
                },
                err -> {
                    context.assertNotNull(err);
                    async.complete();
                }
        );
    }
}
