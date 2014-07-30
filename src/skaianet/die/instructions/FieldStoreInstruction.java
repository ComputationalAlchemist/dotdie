package skaianet.die.instructions;

import skaianet.die.back.ExecutionContext;
import skaianet.die.front.Color;
import skaianet.die.front.ColoredIdentifier;

import java.io.PrintStream;

public class FieldStoreInstruction extends Instruction {
    private final int objectId;
    private final ColoredIdentifier field;
    private final int source;

    public FieldStoreInstruction(Color thread, int objectId, ColoredIdentifier field, int source) {
        super(thread);
        this.objectId = objectId;
        this.field = field;
        this.source = source;
    }

    @Override
    public void printInternal(int indent, PrintStream out) {
        out.println("FIELD " + objectId + "." + field + " <- " + source);
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        executionContext.fieldPut(executionContext.get(objectId), field, executionContext.get(source));
    }
}
