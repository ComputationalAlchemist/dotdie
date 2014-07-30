package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;

import java.io.PrintStream;

public class ThreadBifurcationInstruction extends Instruction {

    private final Color colorA;
    private final Color colorB;

    public ThreadBifurcationInstruction(Color thread, Color colorA, Color colorB) {
        super(thread);
        this.colorA = colorA;
        this.colorB = colorB;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        out.println("BIFURCATE THREAD -> " + colorA + " " + colorB);
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.fork(colorA, colorB);
    }
}
