package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

import java.io.PrintStream;

public class ArrayFetchInstruction implements Instruction {
    private final int target;
    private final int indexRef;

    public ArrayFetchInstruction(int target, int indexRef) {
        this.target = target;
        this.indexRef = indexRef;
    }

    @Override
    public void print(int indent, PrintStream out) {
        out.println("INDEX " + target + "[" + indexRef + "]");
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, executionContext.arrayRef(executionContext.get(target), executionContext.get(indexRef)));
    }
}
