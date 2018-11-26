package net.christophe.genin.domain.monitor.addon.verticles.mustache;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.InputStream;
import java.io.InputStreamReader;


public class ClasspathTemplate extends KnownTemplate {

    public ClasspathTemplate() {
    }

    public ClasspathTemplate(String name, String path, Boolean cache) {
        super(name, path, cache);
    }


    protected Template compile() {
        InputStream inputStream = ClasspathTemplate.class.getResourceAsStream(getPath());

        Mustache.Compiler compiler = Mustache.compiler();
        return compiler.compile(new InputStreamReader(inputStream));

    }
}
