package net.christophe.genin.monitor.domain.server;

import net.christophe.genin.monitor.domain.server.base.DbTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.sql.SQLException;

public class DbWithSchemaTest {

    @BeforeClass
    public static void sBefore() throws SQLException {
        DbTest.startDbServerWithSchema();
    }

    @AfterClass
    public static void sAfter() {
        DbTest.stopDbServer();
    }
}
