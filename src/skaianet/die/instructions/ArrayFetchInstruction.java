package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;

import java.io.PrintStream;

public class ArrayFetchInstruction extends Instruction {
    private final int target;
    private final int indexRef;

    public ArrayFetchInstruction(Color thread, int target, int indexRef) {
        super(thread);
        this.target = target;
        this.indexRef = indexRef;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        out.println("INDEX " + target + "[" + indexRef + "]");
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, executionContext.arrayRef(executionContext.get(target), executionContext.get(indexRef)));
    }
}
