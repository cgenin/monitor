package net.christophe.genin.domain.monitor.addon.verticles.mustache;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemTemplate extends KnownTemplate {


    public FileSystemTemplate() {
    }

    public FileSystemTemplate(String name, String path, Boolean cache) {
        super(name, path, cache);
    }

    @Override
    protected Template compile() {
        try {
            Path p = new File(path).toPath();
            String text = String.join(" ", Files.readAllLines(p));
            Mustache.Compiler compiler = Mustache.compiler();
            return compiler.compile(text);
        } catch (Exception ex) {
            throw new IllegalStateException("Error in compiling", ex);
        }
    }


}
