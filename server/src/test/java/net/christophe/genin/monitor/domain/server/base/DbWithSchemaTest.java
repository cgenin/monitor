package net.christophe.genin.monitor.domain.server.base;

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


}
