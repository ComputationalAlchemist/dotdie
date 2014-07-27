package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

/**
 * Created on 2014-07-25.
 */
public interface Instruction {
    void print();

    void execute(ExecutionContext executionContext); // Return true if backwards branch.
}
