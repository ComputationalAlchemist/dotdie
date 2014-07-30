package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;
import skaianet.die.front.ColoredIdentifier;

import java.io.PrintStream;

public class FieldFetchInstruction extends Instruction {
    private final int target;
    private final ColoredIdentifier field;

    public FieldFetchInstruction(Color thread, int target, ColoredIdentifier field) {
        super(thread);
        this.target = target;
        this.field = field;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        out.println("FIELD " + target + "." + field);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, executionContext.fieldRef(executionContext.get(target), field));
    }
}
