package com.nyx1024.petrified.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Utility class for safely checking API availability via reflection.
 * Prevents ClassNotFoundException and NoSuchMethodException from crashing the plugin.
 */
public final class ApiGuard {

    private ApiGuard() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Check if a class exists on the classpath.
     * @param className The fully qualified class name.
     * @return true if the class can be loaded.
     */
    public static boolean classExists(@NotNull String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Check if a method exists on a class.
     * @param className The fully qualified class name.
     * @param methodName The method name.
     * @param paramTypes The parameter types.
     * @return true if the method exists.
     */
    public static boolean methodExists(@NotNull String className, 
                                       @NotNull String methodName,
                                       @Nullable Class<?>... paramTypes) {
        try {
            Class<?> clazz = Class.forName(className);
            if (paramTypes == null || paramTypes.length == 0) {
                clazz.getDeclaredMethod(methodName);
            } else {
                clazz.getDeclaredMethod(methodName, paramTypes);
            }
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Check if a class has a specific method with any signature.
     * @param clazz The class to check.
     * @param methodName The method name.
     * @return true if any method with that name exists.
     */
    public static boolean hasMethod(@NotNull Class<?> clazz, @NotNull String methodName) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Safely invoke a static method via reflection.
     * @param className The class containing the method.
     * @param methodName The method name.
     * @param returnType The expected return type.
     * @param args Arguments to pass.
     * @return The result, or null if invocation failed.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T invokeStatic(@NotNull String className,
                                     @NotNull String methodName,
                                     @NotNull Class<T> returnType,
                                     Object... args) {
        try {
            Class<?> clazz = Class.forName(className);
            Class<?>[] paramTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
            }
            Method method = clazz.getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            Object result = method.invoke(null, args);
            return returnType.isInstance(result) ? (T) result : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Safely get a field value via reflection.
     * @param instance The object instance.
     * @param fieldName The field name.
     * @return The field value, or null if access failed.
     */
    @Nullable
    public static Object getField(@NotNull Object instance, @NotNull String fieldName) {
        try {
            java.lang.reflect.Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if an enum constant exists.
     * @param enumClass The enum class.
     * @param constantName The constant name.
     * @return true if the constant exists.
     */
    public static boolean enumConstantExists(@NotNull Class<? extends Enum<?>> enumClass,
                                             @NotNull String constantName) {
        try {
            Enum.valueOf(enumClass, constantName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
