package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

import java.io.PrintStream;

public interface Instruction {
    void print(int indent, PrintStream out);

    void execute(ExecutionContext executionContext); // Return true if backwards branch.
}
