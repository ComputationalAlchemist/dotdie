package skaianet.die.back;

import skaianet.die.middle.CompiledProcedure;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Stack;

public class ExecutionContext {
    public final ExecutionExtension extension;
    private final Stack<Frame> stack = new Stack<>();
    private Object outerReturnValue;

    public ExecutionContext(ExecutionExtension extension) {
        this.extension = extension;
    }

    public void init(CompiledProcedure procedure, EnergyPacket packet, Object... arguments) {
        outerReturnValue = null;
        if (arguments.length != procedure.argumentCount) {
            throw new IllegalArgumentException("Bad number of arguments!");
        }
        Object[] variables = new Object[procedure.maxVars];
        variables[0] = packet;
        System.arraycopy(arguments, 0, variables, 1, arguments.length);
        stack.push(new Frame(procedure, variables));
    }

    public boolean runSweep() { // Return true if more sweeps are needed.
        while (runSingle()) ;
        return !stack.isEmpty();
    }

    public Object getReturnValue() {
        return outerReturnValue;
    }

    public boolean runStep() { // Return true if more steps are needed.
        runSingle();
        return !stack.isEmpty();
    }

    private boolean runSingle() { // Return false after each backwards branch instruction or end of procedure.
        if (stack.isEmpty()) {
            return false;
        }
        Frame f = stack.peek();
        if (f.isDone()) {
            stack.pop();
            Object retVal = f.getReturnValue();
            if (stack.isEmpty()) {
                this.outerReturnValue = retVal;
            } else {
                stack.peek().returnValue(this, retVal);
            }
            return false; // Counts as backwards branch.
        }
        int ptr = f.getCodePointer();
        f.execute(this);
        return f == stack.peek() && f.getCodePointer() > ptr;
    }

    public void jump(int label) {
        stack.peek().jump(label);
    }

    public Object get(int i) {
        return stack.peek().get(i);
    }

    public void put(int i, Object o) {
        stack.peek().put(i, o);
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
        } catch (NoSuchFieldException ignored) {
            // Fall through.
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

    public void report(PrintStream out) {
        out.println("Stack Trace:");
        for (Frame frame : stack) {
            frame.report(out);
        }
    }

    public void repeatInstruction() {
        stack.peek().repeatInstruction();
    }
}
