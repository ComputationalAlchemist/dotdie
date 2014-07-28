package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

import java.io.PrintStream;

public class MoveInstruction implements Instruction {
    private final int from;
    private final int to;

    public MoveInstruction(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void print(int indent, PrintStream out) {
        out.println("MOVE " + from + " -> " + to);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(to, executionContext.get(from));
    }
}
