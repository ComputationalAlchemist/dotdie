package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

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

    public ConstantInstruction(int target, Type type, Object value) {
        this.target = target;
        this.type = type;
        this.value = value;
    }

    @Override
    public void print() {
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
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, value);
    }

    public static enum Type {
        NULL, BOOLEAN, STRING, INTEGER
    }
}
