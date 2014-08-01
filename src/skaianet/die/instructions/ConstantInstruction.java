package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;
import skaianet.die.middle.CompiledProcedure;

import java.io.PrintStream;

public class ConstantInstruction extends Instruction {
    private final int target;
    private final Object value;

    public ConstantInstruction(Color thread, int target, Object value) {
        super(thread);
        this.target = target;
        this.value = value;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        if (value instanceof CompiledProcedure) {
            out.println("CONST PROCEDURE -> " + target);
            ((CompiledProcedure) value).print(indent + 1, out);
        } else {
            out.println("CONST " + (value == null ? "NULL" : value.getClass() + ":" + value) + " -> " + target);
        }
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, value);
    }
}
