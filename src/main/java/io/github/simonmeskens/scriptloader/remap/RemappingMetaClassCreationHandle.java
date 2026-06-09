package io.github.simonmeskens.scriptloader.remap;

import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import lombok.Getter;

public class RemappingMetaClassCreationHandle extends MetaClassRegistry.MetaClassCreationHandle {
    @Getter
    private final Remapper remapper;

    public RemappingMetaClassCreationHandle(Remapper remapper) {
        this.remapper = remapper;
    }

    @Override
    protected MetaClass createNormalMetaClass(Class theClass,MetaClassRegistry registry) {
        return new RemappingMetaClass(super.createNormalMetaClass(theClass, registry), remapper);
    }
}
