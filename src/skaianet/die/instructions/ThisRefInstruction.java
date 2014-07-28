package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

import java.io.PrintStream;

/**
 * Created on 2014-07-26.
 */
public class ThisRefInstruction implements Instruction {
    private final int target;

    public ThisRefInstruction(int target) {
        this.target = target;
    }

    @Override
    public void print(int indent, PrintStream out) {
        out.println("THIS -> " + target);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, executionContext.getATHThis());
    }
}
