package skaianet.die.back;

import skaianet.die.front.Color;
import skaianet.die.front.ColoredIdentifier;
import skaianet.die.middle.CompiledProcedure;

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Stack;

public class ExecutionContext {
    public final ExecutionExtension extension;
    public final ExecutionWrapper executionWrapper;
    public final ExecutionContext parent;
    private final Stack<Frame> stack = new Stack<>();
    private Object outerReturnValue;
    private boolean isTerminated = false;

    public ExecutionContext(ExecutionExtension extension, ExecutionWrapper executionWrapper, ExecutionContext parent) {
        this.extension = extension;
        this.executionWrapper = executionWrapper;
        this.parent = parent;
    }

    public void init(CompiledProcedure procedure, EnergyPacket packet, Object... arguments) {
        if (arguments.length != procedure.argumentCount) {
            throw new IllegalArgumentException("Bad number of arguments!");
        }
        Object[] variables = new Object[procedure.maxVars];
        variables[0] = packet;
        System.arraycopy(arguments, 0, variables, 1, arguments.length);
        stack.push(new Frame(Color.NO_THREAD, procedure, variables));
    }

    public void fromFork(ExecutionContext parent, Color thread) {
        Frame oldFrame = parent.stack.peek();
        Frame newFrame = new Frame(thread, oldFrame.procedure, Arrays.copyOf(oldFrame.variables, oldFrame.variables.length));
        newFrame.jump(oldFrame.offset());
        stack.push(newFrame);
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
        return !stack.isEmpty() && f == stack.peek() && f.getCodePointer() > ptr;
    }

    public void jump(int label) {
        stack.peek().jump(label);
    }

    public Object get(int i) {
        return stack.peek().get(i);
    }

    public void put(int i, Object o) {
        if (!stack.isEmpty()) {
            stack.peek().put(i, o);
        }
    }

    public Object calcImport(ColoredIdentifier namespace, ColoredIdentifier name) {
        return extension.calcImport(namespace, name);
    }

    public Object arrayRef(Object array, Object index) {
        if (array.getClass().isArray() && index instanceof Integer) {
            return Array.get(array, (Integer) index);
        } else {
            return extension.arrayRef(array, index);
        }
    }

    public Object fieldRef(Object object, ColoredIdentifier field) {
        try {
            Field javaField = object.getClass().getField(field.name);
            if (javaField.getAnnotation(ATHcessible.class) != null || extension.fieldAccessible(object.getClass(), field, javaField, object)) {
                return javaField.get(object);
            }
        } catch (NoSuchFieldException ignored) {
            // Fall through.
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Expected @ATHcessible field " + object.getClass() + "." + field + " to be accessible!");
        }
        for (Method javaMethod : object.getClass().getMethods()) {
            if (javaMethod.getName().equals(field.name) && (javaMethod.getAnnotation(ATHcessible.class) != null || extension.methodAccessible(object.getClass(), field, javaMethod, object))) {
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

    public Object getATHThis(Color thisColor) {
        if (this.isRootColor(thisColor)) {
            return new ThisObject(this);
        } else {
            return executionWrapper.getThisObject(this.parent, thisColor);
        }
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
        out.println("Stack Trace (" + getRootColor() + "):");
        for (Frame frame : stack) {
            frame.report(out);
        }
    }

    public void repeatInstruction() {
        stack.peek().repeatInstruction();
    }

    public void programRequestedTerminate() {
        stack.clear();
        isTerminated = true;
    }

    public boolean isTerminatedNormally() {
        return isTerminated;
    }

    public void fork(Color a, Color b) {
        ExecutionContext contextA = new ExecutionContext(extension, executionWrapper, this);
        contextA.fromFork(this, a);
        executionWrapper.add(contextA);
        ExecutionContext contextB = new ExecutionContext(extension, executionWrapper, this);
        contextB.fromFork(this, b);
        executionWrapper.add(contextB);
        stack.peek().pauseForThreadRejoin();
        isTerminated = true;
    }

    public boolean isRootColor(Color thisColor) {
        return getRootColor().equals(thisColor);
    }

    public Color getRootColor() {
        return stack.isEmpty() ? null : stack.get(0).thread;
    }
}
