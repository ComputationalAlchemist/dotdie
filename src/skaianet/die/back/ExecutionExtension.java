package skaianet.die.back;

import skaianet.die.front.ColoredIdentifier;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ExecutionExtension {
    Object arrayRef(Object array, Object index);

    boolean fieldAccessible(Class<?> class_, ColoredIdentifier field, Field javaField, Object object);

    Object fieldRef(Object object, ColoredIdentifier field);

    boolean methodAccessible(Class<?> class_, ColoredIdentifier method, Method javaMethod, Object object);

    Object invoke(Object procedure, Object... arguments);

    boolean toBoolean(Object object);

    Object calcImport(ColoredIdentifier namespace, ColoredIdentifier name);

    boolean isAlive(Object object);

    double calcEnergy(Object object);
}
