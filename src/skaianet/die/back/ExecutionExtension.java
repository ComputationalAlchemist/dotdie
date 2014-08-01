package skaianet.die.back;

import skaianet.die.front.ColoredIdentifier;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ExecutionExtension {
    Object arrayRef(Object array, Object index);

    void arrayPut(Object array, Object index, Object value);

    boolean fieldAccessible(Class<?> class_, ColoredIdentifier field, Field javaField, Object object, boolean isWrite);

    Object fieldRef(Object object, ColoredIdentifier field, ExecutionContext context);

    void fieldPut(Object object, ColoredIdentifier field, ExecutionContext context, Object value);

    boolean methodAccessible(Class<?> class_, ColoredIdentifier method, Method javaMethod, Object object);

    Object invoke(Object procedure, ExecutionContext context, Object... arguments);

    boolean toBoolean(Object object);

    Object calcImport(ColoredIdentifier namespace, ColoredIdentifier name);

    boolean isAlive(Object object);

    double calcEnergy(Object object);
}
