package net.christophe.genin.domain.monitor.addon.verticles;

import io.vertx.rxjava.core.AbstractVerticle;

import java.util.Collection;

public class PdfVerticleBuilder implements MustacheBuilder<PdfVerticleBuilder> {

    private final MustacheVerticleBuilder mvb = MustacheVerticleBuilder.create();

    public static PdfVerticleBuilder create() {
        return new PdfVerticleBuilder();
    }

    private PdfVerticleBuilder() {
    }

    @Override
    public PdfVerticleBuilder fromFileSystemTemplates(String... paths) {
        mvb.fromFileSystemTemplates(paths);
        return this;
    }

    @Override
    public PdfVerticleBuilder fromFileSystemTemplates(Collection<String> paths) {
        mvb.fromFileSystemTemplates(paths);
        return this;
    }

    @Override
    public PdfVerticleBuilder fromFileSystemFolder(String... paths) {
        mvb.fromFileSystemFolder(paths);
        return this;
    }

    @Override
    public PdfVerticleBuilder fromFileSystemFolder(Collection<String> paths) {
        mvb.fromFileSystemFolder(paths);
        return this;
    }

    @Override
    public PdfVerticleBuilder cache(boolean cache) {
        mvb.cache(cache);
        return this;
    }

    @Override
    public PdfVerticleBuilder withClasspathTemplate(String... templates) {
        mvb.withClasspathTemplate(templates);
        return this;
    }

    @Override
    public PdfVerticleBuilder withClasspathTemplate(Collection<String> templates) {
        mvb.withClasspathTemplate(templates);
        return this;
    }

    @Override
    public AbstractVerticle build() {
        return new PdfVerticle(mvb, this);
    }
}
