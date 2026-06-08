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
    public String remapFieldName(String dstClassName, String fieldName) {
        MappingTreeView.ClassMappingView classMapping = mappings.getClass(toMappingName(dstClassName), dstNSId);
        if (classMapping == null) return fieldName;
        MappingTreeView.FieldMappingView fieldMapping = classMapping.getField(fieldName, null, srcNSId);
        if (fieldMapping == null) return fieldName;
        return fieldMapping.getName(dstNSId);
    }

    @Override
    public String remapMethodName(String dstClassName, String methodName) {
        MappingTreeView.ClassMappingView classMapping = mappings.getClass(toMappingName(dstClassName), dstNSId);
        if (classMapping == null) return methodName;
        MappingTreeView.MethodMappingView methodMapping = classMapping.getMethod(methodName, null, srcNSId);
        if (methodMapping == null) return methodName;
        return methodMapping.getName(dstNSId);
    }

    private String toMappingName(String name) {
        return name.replace('.', '/');
    }

    private String fromMappingName(String name) {
        return name.replace('/', '.');
    }
}
