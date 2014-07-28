package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;

import java.io.PrintStream;

public class FieldFetchInstruction implements Instruction {
    private final int target;
    private final String field;

    public FieldFetchInstruction(int target, String field) {
        this.target = target;
        this.field = field;
    }

    @Override
    public void print(int indent, PrintStream out) {
        out.println("FIELD " + target + "." + field);
    }

    @Override
    public void execute(ExecutionContext executionContext) {
        executionContext.put(target, executionContext.fieldRef(executionContext.get(target), field));
    }
}
