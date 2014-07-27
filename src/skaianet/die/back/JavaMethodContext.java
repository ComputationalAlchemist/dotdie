package skaianet.die.back;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created on 2014-07-26.
 */
class JavaMethodContext {
    private final Object object;
    private final String method;
    private final ExecutionContext context;

    public JavaMethodContext(Object object, String method, ExecutionContext context) {
        this.object = object;
        this.method = method;
        this.context = context;
    }

    public Object invoke(Object... arguments) {
        outer:
        for (Method m : object.getClass().getMethods()) {
            if (!m.getName().equals(method) || (m.getAnnotation(ATHcessible.class) == null && !context.extension.methodAccessible(object.getClass(), method, m, object))) {
                continue;
            }
            Class<?>[] parameterTypes = m.getParameterTypes();
            if (parameterTypes.length != arguments.length) {
                continue;
            }
            for (int i = 0; i < parameterTypes.length; i++) {
                if (arguments[i] != null && !parameterMatches(parameterTypes[i], arguments[i].getClass())) {
                    continue outer;
                }
            }
            try {
                return m.invoke(object, arguments);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Expected @ATHcessible method " + object.getClass() + "." + method + " to be accessible!");
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        Class<?>[] types = new Class<?>[arguments.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = arguments[i] == null ? null : arguments[i].getClass();
        }
        throw new IllegalArgumentException("No matching method for " + object + "." + method + Arrays.toString(types) + "!");
    }

    private boolean parameterMatches(Class<?> parameterType, Class<?> argumentType) {
        if (parameterType.isAssignableFrom(argumentType)) {
            return true;
        } else if (parameterType.isPrimitive()) {
            if (parameterType == int.class) {
                return argumentType == Integer.class;
            } else if (parameterType == long.class) {
                return argumentType == Long.class;
            } else if (parameterType == short.class) {
                return argumentType == Short.class;
            } else if (parameterType == byte.class) {
                return argumentType == Byte.class;
            } else if (parameterType == char.class) {
                return argumentType == Character.class;
            } else if (parameterType == double.class) {
                return argumentType == Double.class;
            } else if (parameterType == float.class) {
                return argumentType == Float.class;
            } else if (parameterType == boolean.class) {
                return argumentType == Boolean.class;
            }
        }
        return false;
    }
}
