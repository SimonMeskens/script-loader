package io.github.simonmeskens.scriptloader.remap;

import groovy.lang.GroovyClassLoader;
import lombok.Getter;
import org.codehaus.groovy.control.CompilationFailedException;

public class RemappingClassLoader extends GroovyClassLoader {
    @Getter
    private final Remapper remapper;

    public RemappingClassLoader(ClassLoader parent, Remapper remapper) {
        super(parent);
        this.remapper = remapper;
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException, CompilationFailedException {
        return loadClass(name, true, true, resolve);
    }

    @Override
    public Class<?> loadClass(final String name, final boolean lookupScriptFiles, final boolean preferClassOverScript) throws ClassNotFoundException, CompilationFailedException {
        return loadClass(name, lookupScriptFiles, preferClassOverScript, false);
    }

    @Override
    public Class<?> loadClass(final String name, final boolean lookupScriptFiles, final boolean preferClassOverScript, final boolean resolve) throws ClassNotFoundException, CompilationFailedException {
        return super.loadClass(remapper.remapClassName(name), lookupScriptFiles, preferClassOverScript, resolve);
    }
}
