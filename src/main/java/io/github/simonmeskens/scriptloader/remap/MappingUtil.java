package io.github.simonmeskens.scriptloader.remap;

import org.apache.commons.lang3.StringUtils;

public class MappingUtil {
    public static String toMappingName(String name) {
        return name.replace('.', '/');
    }

    public static String fromMappingName(String name) {
        return name.replace('/', '.');
    }

    public static String toGetterName(String prop) {
        if (prop == null || prop.isEmpty()) return prop;
        return "get" + StringUtils.capitalize(prop);
    }

    public static String toSetterName(String prop) {
        if (prop == null || prop.isEmpty()) return prop;
        return "set" + StringUtils.capitalize(prop);
    }

    public static boolean hasField(Class<?> owner, String fieldName) {
        while (owner != null) {
            try {
                owner.getDeclaredField(fieldName);
                return true;
            } catch (NoSuchFieldException ignored) {
                owner = owner.getSuperclass();
            }
        }

        return false;
    }

    public static boolean hasMethod(Class<?> owner, String fieldName) {
        while (owner != null) {
            try {
                owner.getDeclaredMethod(fieldName);
                return true;
            } catch (NoSuchMethodException ignored) {
                owner = owner.getSuperclass();
            }
        }

        return false;
    }
}
