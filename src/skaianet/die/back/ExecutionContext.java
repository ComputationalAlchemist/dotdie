package skaianet.die.back;

import skaianet.die.middle.CompiledProcedure;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created on 2014-07-26.
 */
public class ExecutionContext {
    public final ExecutionExtension extension;
    private final CompiledProcedure procedure;
    private Object[] variables;
    private int codePointer;

    public ExecutionContext(CompiledProcedure procedure, ExecutionExtension extension) {
        this.procedure = procedure;
        this.extension = extension;
    }

    public void init(EnergyPacket packet, Object... arguments) {
        variables = null;
        if (arguments.length != procedure.argcount) {
            throw new IllegalArgumentException("Bad number of arguments!");
        }
        this.codePointer = 0;
        Object[] out = new Object[procedure.maxVars];
        out[0] = packet;
        System.arraycopy(arguments, 0, out, 1, arguments.length);
        variables = out;
    }

    public boolean runSweep() { // Return true if more sweeps are needed.
        while (runSingle()) ;
        return codePointer < procedure.instructions.length;
    }

    public boolean runStep() { // Return true if more steps are needed.
        runSingle();
        return codePointer < procedure.instructions.length;
    }

    private boolean runSingle() { // Return false after each backwards branch instruction or end of procedure.
        if (codePointer >= procedure.instructions.length) {
            return false;
        }
        int ptr = codePointer;
        procedure.instructions[codePointer++].execute(this);
        return codePointer > ptr;
    }

    public void jump(int label) {
        codePointer = label;
    }

    public Object get(int i) {
        return variables[i];
    }

    public void put(int i, Object o) {
        variables[i] = o;
    }

    public Object calcImport(String namespace, String name) {
        return extension.calcImport(namespace, name);
    }

    public Object arrayRef(Object array, Object index) {
        if (array.getClass().isArray() && index instanceof Integer) {
            return Array.get(array, (Integer) index);
        } else {
            return extension.arrayRef(array, index);
        }
    }

    public Object fieldRef(Object object, String field) {
        try {
            Field javaField = object.getClass().getField(field);
            if (javaField.getAnnotation(ATHcessible.class) != null || extension.fieldAccessible(object.getClass(), field, javaField, object)) {
                return javaField.get(object);
            }
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Expected @ATHcessible field " + object.getClass() + "." + field + " to be accessible!");
        }
        for (Method javaMethod : object.getClass().getMethods()) {
            if (javaMethod.getName().equals(field) && (javaMethod.getAnnotation(ATHcessible.class) != null || extension.methodAccessible(object.getClass(), field, javaMethod, object))) {
                return new JavaMethodContext(object, field, this);
            }
        }
        return extension.fieldRef(object, field);
    }

    public Object invoke(Object procedure, Object[] arguments) {
        if (procedure instanceof JavaMethodContext) {
            return ((JavaMethodContext) procedure).invoke(arguments);
        }
        return extension.invoke(procedure, arguments);
    }

    public boolean is(Object object) {
        if (object instanceof Boolean) {
            return (Boolean) object;
        } else if (object instanceof Number) {
            return ((Number) object).doubleValue() == 0;
        } else {
            return extension.toBoolean(object);
        }
    }

    public Object getATHThis() {
        return this;
    }

    public LoopContext loopContext(Object object) {
        return new LoopContext(object, this);
    }

    public boolean isAlive(Object object) {
        if (object instanceof ATHAlive) {
            return ((ATHAlive) object).isAlive();
        } else if (object == null) {
            return false;
        } else if (object instanceof Boolean) {
            return (Boolean) object;
        }
        return extension.isAlive(object);
    }

    public double calcEnergy(Object object) { // in Joules
        if (object instanceof ATHAlive) {
            return ((ATHAlive) object).getEnergy();
        }
        return extension.calcEnergy(object);
    }

    public void report() {
        System.out.println("Execution is at " + codePointer + " with " + Arrays.toString(variables));
    }
}
