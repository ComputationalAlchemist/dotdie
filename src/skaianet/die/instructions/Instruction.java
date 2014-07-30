package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;

import java.io.PrintStream;

public abstract class Instruction {
    public final Color thread;

    public Instruction(Color thread) {
        if (thread == null) {
            throw new NullPointerException();
        }
        this.thread = thread;
    }

    public void print(int indent, PrintStream out) {
        out.print(thread + "\t");
        printInternal(indent, out);
    }

    public abstract void printInternal(int indent, PrintStream out);

    public void execute(Color thread, ExecutionContext executionContext) {
        // Return true if backwards branch.
        if (shouldExecute(thread)) {
            execute(executionContext);
        }
    }

    public boolean shouldExecute(Color thread) {
        return this.thread.equals(Color.NO_THREAD) || this.thread.equals(thread);
    }

    protected abstract void execute(ExecutionContext executionContext); // Return true if backwards branch.
}
