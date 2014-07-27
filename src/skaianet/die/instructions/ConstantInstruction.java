package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.middle.CompiledProcedure;

/**
 * Created on 2014-07-25.
 */
public class ConstantInstruction implements Instruction {
    private final int target;
    private final Type type;
    private final Object value;

    public ConstantInstruction(int target, boolean constant) {
        this(target, Type.BOOLEAN, constant);
    }

    public ConstantInstruction(int target, String constant) {
        this(target, Type.STRING, constant);
    }

    public ConstantInstruction(int target, int constant) {
        this(target, Type.INTEGER, constant);
    }

    public ConstantInstruction(int target, CompiledProcedure constant) {
        this(target, Type.PROCEDURE, constant);
    }

    public ConstantInstruction(int target, Type type, Object value) {
        this.target = target;
        this.type = type;
        this.value = value;
    }

    @Override
    public void print(int indent) {
        switch (type) {
            case NULL:
                System.out.println("CONST NULL -> " + target);
                break;
            case BOOLEAN:
                System.out.println("CONST " + value + " -> " + target);
                break;
            case STRING:
                System.out.println("CONST `" + value + "' -> " + target);
                break;
            case INTEGER:
                System.out.println("CONST #" + value + " -> " + target);
                break;
            case PROCEDURE:
                System.out.println("CONST PROCEDURE -> " + target);
                ((CompiledProcedure) value).print(indent + 1);
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
