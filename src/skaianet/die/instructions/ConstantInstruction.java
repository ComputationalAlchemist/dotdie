package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;
import skaianet.die.middle.CompiledProcedure;

import java.io.PrintStream;

public class ConstantInstruction extends Instruction {
    private final int target;
    private final Type type;
    private final Object value;

    public ConstantInstruction(Color thread, int target, boolean constant) {
        this(thread, target, Type.BOOLEAN, constant);
    }

    public ConstantInstruction(Color thread, int target, String constant) {
        this(thread, target, Type.STRING, constant);
    }

    public ConstantInstruction(Color thread, int target, int constant) {
        this(thread, target, Type.INTEGER, constant);
    }

    public ConstantInstruction(Color thread, int target, CompiledProcedure constant) {
        this(thread, target, Type.PROCEDURE, constant);
    }

    public ConstantInstruction(Color thread, int target, Type type, Object value) {
        super(thread);
        this.target = target;
        this.type = type;
        this.value = value;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        switch (type) {
            case NULL:
                out.println("CONST NULL -> " + target);
                break;
            case BOOLEAN:
                out.println("CONST " + value + " -> " + target);
                break;
            case STRING:
                out.println("CONST `" + value + "' -> " + target);
                break;
            case INTEGER:
                out.println("CONST #" + value + " -> " + target);
                break;
            case PROCEDURE:
                out.println("CONST PROCEDURE -> " + target);
                ((CompiledProcedure) value).print(indent + 1, out);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, value);
    }

    public static enum Type {
        NULL, BOOLEAN, STRING, PROCEDURE, INTEGER
    }
}
