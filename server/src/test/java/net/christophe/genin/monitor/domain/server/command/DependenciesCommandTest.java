package net.christophe.genin.monitor.domain.server.command;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DependenciesCommandTest {

    @Test
    public void should_sanitize_lib_java() {
        assertEquals("DEPENDENCIES", new DependenciesCommand.DependenciesSanitizer("DEPENDENCIES-manager").run());
        assertEquals("DEPENDENCIES", new DependenciesCommand.DependenciesSanitizer("DEPENDENCIES-service-client").run());
        assertEquals("TRUC", new DependenciesCommand.DependenciesSanitizer("truc-service").run());
        assertEquals("TRUC-TEST", new DependenciesCommand.DependenciesSanitizer("truc-test").run());
    }
}
