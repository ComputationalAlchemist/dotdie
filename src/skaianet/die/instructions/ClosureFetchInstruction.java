package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;

import java.io.PrintStream;

public class ClosureFetchInstruction extends Instruction {
    private final int upvalue;
    private final int target;

    public ClosureFetchInstruction(Color thread, int upvalue, int target) {
        super(thread);
        this.upvalue = upvalue;
        this.target = target;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        out.println("UPVALUE " + upvalue + " -> " + target);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, executionContext.getUpvalue(upvalue));
    }
}
