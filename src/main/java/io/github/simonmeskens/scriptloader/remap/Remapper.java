package io.github.simonmeskens.scriptloader.remap;

public interface Remapper {
    String remapClassName(String className);

    String remapFieldName(Class<?> clazz, String fieldName);

    String remapMethodName(Class<?> clazz, String methodName);
}
