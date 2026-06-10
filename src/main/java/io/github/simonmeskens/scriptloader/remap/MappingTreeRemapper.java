package io.github.simonmeskens.scriptloader.remap;

import io.github.simonmeskens.scriptloader.GroovyScriptLoader;
import lombok.Getter;
import net.fabricmc.mappingio.tree.MappingTreeView;
import net.fabricmc.mappingio.tree.VisitableMappingTree;

import static io.github.simonmeskens.scriptloader.remap.MappingUtil.*;

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
            GroovyScriptLoader.logger.info("Trying to remap field {} ({})", fieldName, clazz.getName());
            MappingTreeView.ClassMappingView classMapping = mappings.getClass(toMappingName(clazz.getName()), dstNSId);
            if (classMapping == null) continue;
            MappingTreeView.FieldMappingView fieldMapping = classMapping.getField(fieldName, null, srcNSId);
            if (fieldMapping == null) continue;
            String remapped = fieldMapping.getName(dstNSId);
            GroovyScriptLoader.logger.info("Found field {}.{} ({}.{})", clazz.getName(), remapped, fromMappingName(classMapping.getName(srcNSId)), fieldName);
            return remapped;
        }

        return null;
    }

    @Override
    public String remapMethodName(Class<?> clazz, String methodName) {
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            GroovyScriptLoader.logger.info("Trying to remap method {} ({})", methodName, clazz.getName());
            MappingTreeView.ClassMappingView classMapping = mappings.getClass(toMappingName(clazz.getName()), dstNSId);
            if (classMapping == null) continue;
            MappingTreeView.MethodMappingView methodMapping = classMapping.getMethod(methodName, null, srcNSId);
            if (methodMapping == null) continue;
            String remapped = methodMapping.getName(dstNSId);
            GroovyScriptLoader.logger.info("Found method {}.{} ({}.{})", clazz.getName(), remapped, fromMappingName(classMapping.getName(srcNSId)), methodName);
            return remapped;
        }

        return null;
    }
}
