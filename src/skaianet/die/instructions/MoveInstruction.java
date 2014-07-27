package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

/**
 * Created on 2014-07-25.
 */
public class MoveInstruction implements Instruction {
    private final int from;
    private final int to;

    public MoveInstruction(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void print() {
        System.out.println("MOVE " + from + " -> " + to);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(to, executionContext.get(from));
    }
}
