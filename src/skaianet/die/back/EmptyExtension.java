package skaianet.die.back;

import skaianet.die.front.ColoredIdentifier;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EmptyExtension implements ExecutionExtension {
    @Override
    public Object arrayRef(Object array, Object index) {
        throw new IllegalArgumentException("Cannot index " + array);
    }

    @Override
    public boolean fieldAccessible(Class<?> class_, ColoredIdentifier field, Field javaField, Object object) {
        return false;
    }

    @Override
    public Object fieldRef(Object object, ColoredIdentifier field, ExecutionContext context) {
        throw new IllegalArgumentException("No such field " + field + " on " + object);
    }

    @Override
    public boolean methodAccessible(Class<?> class_, ColoredIdentifier method, Method javaMethod, Object object) {
        return false;
    }

    @Override
    public Object invoke(Object procedure, ExecutionContext context, Object... arguments) {
        throw new IllegalArgumentException("Not invokable: " + procedure);
    }

    @Override
    public boolean toBoolean(Object object) {
        return true;
    }

    @Override
    public Object calcImport(ColoredIdentifier namespace, ColoredIdentifier name) {
        throw new IllegalArgumentException("No such namespace " + namespace);
    }

    @Override
    public boolean isAlive(Object object) {
        return false;
    }

    @Override
    public double calcEnergy(Object object) {
        return 0;
    }
}
