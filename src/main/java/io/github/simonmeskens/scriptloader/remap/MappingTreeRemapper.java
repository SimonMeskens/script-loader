package io.github.simonmeskens.scriptloader.remap;

import lombok.Getter;
import net.fabricmc.mappingio.tree.MappingTreeView;
import net.fabricmc.mappingio.tree.VisitableMappingTree;

public class MappingTreeRemapper implements Remapper {
    @Getter
    private final VisitableMappingTree mappings;
    private final int srcNSId;
    private final int dstNSId;

    public MappingTreeRemapper(VisitableMappingTree mappings, String srcNS, String dstNS) {
        this.mappings = mappings;
        this.srcNSId = mappings.getNamespaceId(srcNS);
        this.dstNSId = mappings.getNamespaceId(dstNS);
    }

    public String getSrcNamespace() {
        return mappings.getNamespaceName(this.srcNSId);
    }

    public String getDstNamespace() {
        return mappings.getNamespaceName(this.dstNSId);
    }

    @Override
    public String remapClassName(String className) {
        return fromMappingName(mappings.mapClassName(toMappingName(className), this.srcNSId, this.dstNSId));
    }

    @Override
    public String remapFieldName(Class<?> clazz, String fieldName) {
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            MappingTreeView.ClassMappingView classMapping = mappings.getClass(toMappingName(clazz.getName()), dstNSId);
            if (classMapping == null) continue;
            MappingTreeView.FieldMappingView fieldMapping = classMapping.getField(fieldName, null, srcNSId);
            if (fieldMapping == null) continue;
            return fieldMapping.getName(dstNSId);
        }
        return fieldName;
    }

    @Override
    public String remapMethodName(Class<?> clazz, String methodName) {
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            MappingTreeView.ClassMappingView classMapping = mappings.getClass(toMappingName(clazz.getName()), dstNSId);
            if (classMapping == null) continue;
            MappingTreeView.MethodMappingView methodMapping = classMapping.getMethod(methodName, null, srcNSId);
            if (methodMapping == null) continue;
            return methodMapping.getName(dstNSId);
        }
        return methodName;
    }

    private String toMappingName(String name) {
        return name.replace('.', '/');
    }

    private String fromMappingName(String name) {
        return name.replace('/', '.');
    }
}
