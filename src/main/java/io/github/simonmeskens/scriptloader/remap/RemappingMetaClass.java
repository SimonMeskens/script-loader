package io.github.simonmeskens.scriptloader.remap;

import groovy.lang.*;
import io.github.simonmeskens.scriptloader.GroovyScriptLoader;
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
        try {
            return super.invokeMethod(object, name, args);
        } catch(MissingMethodException ignored) {}

        GroovyScriptLoader.logger.info("Dynamically invoking {}.{}", delegate.getTheClass().getName(), name);
        String remapped = remapper.remapMethodName(delegate.getTheClass(), name);

        if (remapped != null) {
            MetaMethod mm = super.getMetaMethod(remapped, args);
            if (mm != null) {
                return mm.invoke(object, args);
            } else {
                return super.invokeMethod(object, remapped, args);
            }
        }

        return super.invokeMethod(object, name, args);
    }

    @Override
    public Object invokeStaticMethod(Object owner, String name, Object[] args) {
        try {
            return super.invokeStaticMethod(owner, name, args);
        } catch(MissingMethodException ignored) {}

        GroovyScriptLoader.logger.info("Dynamically statically invoking {}.{}", delegate.getTheClass().getName(), name);
        String remapped = remapper.remapMethodName(delegate.getTheClass(), name);

        if (remapped != null) {
            return super.invokeStaticMethod(owner, remapped, args);
        }

        return super.invokeStaticMethod(owner, name, args);
    }

    @Override
    public Object getProperty(Object object, String property) {
        try {
            super.getProperty(object, property);
        } catch(MissingPropertyException ignored) {}

        GroovyScriptLoader.logger.info("Dynamically getting {}.{}", delegate.getTheClass().getName(), property);
        String remapped = remapper.remapFieldName(delegate.getTheClass(), property);

        if (remapped != null) {
            return super.getProperty(object, remapped);
        }

        remapped = remapper.remapMethodName(delegate.getTheClass(), MappingUtil.toGetterName(property));

        if (remapped != null) {
            MetaMethod mm = super.getMetaMethod(remapped, new Object[0]);
            if (mm != null) {
                return mm.invoke(object, new Object[0]);
            } else {
                return super.invokeMethod(object, remapped, new Object[0]);
            }
        }

        return super.getProperty(object, property);
    }

    @Override
    public void setProperty(Object object, String property, Object newValue) {
        try {
            super.setProperty(object, property, newValue);
        } catch(MissingPropertyException ignored) {}

        GroovyScriptLoader.logger.info("Dynamically setting {}.{}", delegate.getTheClass().getName(), property);
        String remapped = remapper.remapFieldName(delegate.getTheClass(), property);

        if (remapped != null) {
            super.setProperty(object, remapped, newValue);
            return;
        }

        remapped = remapper.remapMethodName(delegate.getTheClass(), MappingUtil.toSetterName(property));

        if (remapped != null) {
            MetaMethod mm = super.getMetaMethod(remapped, new Object[] { newValue });
            if (mm != null) {
                mm.invoke(object, new Object[0]);
            } else {
                super.invokeMethod(object, remapped, new Object[] { newValue });
            }
            return;
        }

        super.setProperty(object, property, newValue);
    }
}
