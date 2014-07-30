package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;

import java.io.PrintStream;

public class ThisRefInstruction extends Instruction {
    private final int target;
    private final Color thisColor;

    public ThisRefInstruction(Color thread, int target, Color thisColor) {
        super(thread);
        this.target = target;
        this.thisColor = thisColor;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        out.println("THIS " + thisColor + " -> " + target);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, executionContext.getATHThis(thisColor));
    }
}
