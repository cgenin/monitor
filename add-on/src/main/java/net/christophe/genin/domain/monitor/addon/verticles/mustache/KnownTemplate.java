package net.christophe.genin.domain.monitor.addon.verticles.mustache;

import com.samskivert.mustache.Template;

import java.util.Optional;

public abstract class KnownTemplate {

    protected Boolean cache;
    protected String name;
    protected String path;
    private Template compiler;


    public KnownTemplate() {
    }

    public KnownTemplate(String name, String path, Boolean cache) {
        this.name = name;
        this.path = path;
        this.cache = cache;

    }

    public KnownTemplate setName(String name) {
        this.name = name;
        return this;
    }

    public KnownTemplate setPath(String path) {
        this.path = path;
        return this;
    }

    public KnownTemplate setCache(Boolean cache) {
        this.cache = cache;
        return this;
    }

    public Boolean getCache() {
        return cache;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public KnownTemplate registered() {
        if (!cache)
            return this;
        this.compiler = compile();
        return this;
    }

    public Template getCompiler() {
        return Optional.ofNullable(compiler).orElseGet(this::compile);
    }

    protected abstract Template compile();
}
