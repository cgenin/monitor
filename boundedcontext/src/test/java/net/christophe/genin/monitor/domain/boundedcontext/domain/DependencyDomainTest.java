package net.christophe.genin.monitor.domain.boundedcontext.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class DependencyDomainTest {
    @Test
    public void should_sanitize_lib_java() {
        assertEquals("DEPENDENCIES", new DependencyDomain.DependenciesSanitizer("DEPENDENCIES-manager").run());
        assertEquals("DEPENDENCIES", new DependencyDomain.DependenciesSanitizer("DEPENDENCIES-service-client").run());
        assertEquals("TRUC", new DependencyDomain.DependenciesSanitizer("truc-service").run());
        assertEquals("TRUC-TEST", new DependencyDomain.DependenciesSanitizer("truc-test").run());
    }
}
