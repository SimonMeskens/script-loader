package io.github.simonmeskens.scriptloader.remap;

public interface Remapper {
    String remapClassName(String className);

    String remapFieldName(String className, String fieldName);

    String remapMethodName(String className, String methodName);
}
