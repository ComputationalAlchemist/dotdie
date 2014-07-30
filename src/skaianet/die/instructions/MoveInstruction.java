package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;

import java.io.PrintStream;

public class MoveInstruction extends Instruction {
    private final int from;
    private final int to;

    public MoveInstruction(Color thread, int from, int to) {
        super(thread);
        this.from = from;
        this.to = to;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        out.println("MOVE " + from + " -> " + to);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(to, executionContext.get(from));
    }
}
