package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

import java.io.PrintStream;

/**
 * Created on 2014-07-25.
 */
public interface Instruction {
    void print(int indent, PrintStream out);

    void execute(ExecutionContext executionContext); // Return true if backwards branch.
}
