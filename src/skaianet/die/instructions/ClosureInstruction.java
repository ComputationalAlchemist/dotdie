package skaianet.die.instructions;

import skaianet.die.back.Closure;
import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;
import skaianet.die.middle.CompiledProcedure;

import java.io.PrintStream;
import java.util.Arrays;

public class ClosureInstruction extends Instruction {
    private final int target;
    private final CompiledProcedure procedure;
    private final int[] mapping;

    public ClosureInstruction(Color thread, int target, CompiledProcedure procedure, int[] mapping) {
        super(thread);
        this.target = target;
        this.procedure = procedure;
        this.mapping = mapping;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        out.println("CLOSURE PROCEDURE -> " + target + " " + Arrays.toString(mapping));
        procedure.print(indent + 1, out);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        Object[] upvalues = new Object[mapping.length];
        for (int i = 0; i < mapping.length; i++) {
            upvalues[i] = executionContext.get(mapping[i]);
        }
        executionContext.put(target, new Closure(procedure, upvalues));
    }
}
