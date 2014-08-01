package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;

import java.io.PrintStream;

public class ArrayStoreInstruction extends Instruction {
    private final int arrayId;
    private final int indexRef;
    private final int source;

    public ArrayStoreInstruction(Color thread, int arrayId, int indexRef, int source) {
        super(thread);
        this.arrayId = arrayId;
        this.indexRef = indexRef;
        this.source = source;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        out.println("INDEX " + arrayId + "[" + indexRef + "] <- " + source);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.arrayPut(executionContext.get(arrayId), executionContext.get(indexRef), executionContext.get(source));
    }
}
