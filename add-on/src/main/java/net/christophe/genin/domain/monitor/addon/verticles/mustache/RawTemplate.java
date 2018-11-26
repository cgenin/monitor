package net.christophe.genin.domain.monitor.addon.verticles.mustache;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.StringReader;
import java.util.Objects;

public class RawTemplate extends KnownTemplate {
    private final String raw;

    public RawTemplate(String name, String raw, Boolean cache) {
        super(name, "Raw", cache);
        Objects.requireNonNull(raw);
        this.raw = raw;
    }

    @Override
    protected Template compile() {
        Mustache.Compiler compiler = Mustache.compiler();
        return compiler.compile(new StringReader(raw));
    }
}
