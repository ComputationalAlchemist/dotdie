package skaianet.die.back;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ExecutionExtension {
    Object arrayRef(Object array, Object index);

    boolean fieldAccessible(Class<?> class_, String field, Field javaField, Object object);

    Object fieldRef(Object object, String field);

    boolean methodAccessible(Class<?> class_, String method, Method javaMethod, Object object);

    Object invoke(Object procedure, Object... arguments);

    boolean toBoolean(Object object);

    Object calcImport(String namespace, String name);

    boolean isAlive(Object object);

    double calcEnergy(Object object);
}
