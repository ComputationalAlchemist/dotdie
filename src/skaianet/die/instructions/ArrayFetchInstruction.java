package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

/**
 * Created on 2014-07-25.
 */
public class ArrayFetchInstruction implements Instruction {
    private final int target;
    private final int indexRef;

    public ArrayFetchInstruction(int target, int indexRef) {
        this.target = target;
        this.indexRef = indexRef;
    }

    @Override
    public void print() {
        System.out.println("INDEX " + target + "[" + indexRef + "]");
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, executionContext.arrayRef(executionContext.get(target), executionContext.get(indexRef)));
    }
}
