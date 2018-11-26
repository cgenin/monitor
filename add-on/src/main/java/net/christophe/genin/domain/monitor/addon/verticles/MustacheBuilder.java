package net.christophe.genin.domain.monitor.addon.verticles;

import io.vertx.rxjava.core.AbstractVerticle;

import java.util.Collection;

public interface MustacheBuilder<T> {
    T fromFileSystemTemplates(String... paths);

    T fromFileSystemTemplates(Collection<String> paths);

    T fromFileSystemFolder(String... paths);

    T fromFileSystemFolder(Collection<String> paths);

    T cache(boolean cache);

    T withClasspathTemplate(String... templates);

    T withClasspathTemplate(Collection<String> templates);

    AbstractVerticle build();
}
