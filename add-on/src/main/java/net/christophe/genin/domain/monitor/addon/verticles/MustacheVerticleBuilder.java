package net.christophe.genin.domain.monitor.addon.verticles;

import io.vertx.rxjava.core.AbstractVerticle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class MustacheVerticleBuilder implements MustacheBuilder<MustacheVerticleBuilder> {

    protected final List<String> systemfilesTemplates = new ArrayList<>();
    protected final List<String> systemfilesFolders = new ArrayList<>();
    protected final List<String> classpathTemplates = new ArrayList<>();
    protected boolean cache = true;

    public static MustacheVerticleBuilder create() {
        return new MustacheVerticleBuilder();
    }

    private MustacheVerticleBuilder() {
    }

    @Override
    public MustacheVerticleBuilder fromFileSystemTemplates(String... paths) {
        return fromFileSystemTemplates(Arrays.asList(paths));
    }

    @Override
    public MustacheVerticleBuilder fromFileSystemTemplates(Collection<String> paths) {
        systemfilesTemplates.addAll(paths);
        return this;
    }

    @Override
    public MustacheVerticleBuilder fromFileSystemFolder(String... paths) {
        return fromFileSystemFolder(Arrays.asList(paths));
    }

    @Override
    public MustacheVerticleBuilder fromFileSystemFolder(Collection<String> paths) {
        systemfilesFolders.addAll(paths);
        return this;
    }

    @Override
    public MustacheVerticleBuilder cache(boolean cache) {
        this.cache = cache;
        return this;
    }

    @Override
    public MustacheVerticleBuilder withClasspathTemplate(String... templates) {
        return withClasspathTemplate(Arrays.asList(templates));
    }

    @Override
    public MustacheVerticleBuilder withClasspathTemplate(Collection<String> templates) {
        this.classpathTemplates.addAll(templates);
        return this;
    }


    @Override
    public AbstractVerticle build() {
        return new MustacheVerticle(this);
    }



}
