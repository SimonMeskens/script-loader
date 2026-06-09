package io.github.simonmeskens.scriptloader.remap;

import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;
import lombok.Getter;

public final class RemappingMetaClass extends DelegatingMetaClass {
    @Getter
    private final Remapper remapper;
    @Getter
    private final MetaClass delegate;

    public RemappingMetaClass(MetaClass delegate, Remapper remapper) {
        super(delegate);
        this.remapper = remapper;
        this.delegate = delegate;
        initialize();
    }

    @Override
    public Object invokeMethod(Object object, String name, Object[] args) {
        return super.invokeMethod(object, remapper.remapMethodName(delegate.getTheClass(), name), args);
    }

    @Override
    public Object invokeStaticMethod(Object owner, String name, Object[] args) {
        return super.invokeStaticMethod(owner, remapper.remapMethodName(delegate.getTheClass(), name), args);
    }

    @Override
    public Object getProperty(Object object, String property) {
        return super.getProperty(object, remapper.remapFieldName(delegate.getTheClass(), property));
    }

    @Override
    public void setProperty(Object object, String property, Object newValue) {
        super.setProperty(object, remapper.remapFieldName(delegate.getTheClass(), property), newValue);
    }
}
